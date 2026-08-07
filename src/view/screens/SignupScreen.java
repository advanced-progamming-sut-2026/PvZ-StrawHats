package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import controller.menus.authentication.SignupMenu;

import java.util.List;

public class SignupScreen extends AuthScreen {

    private enum Step { REGISTER, SECURITY_QUESTION }

    private Step step = Step.REGISTER;

    @Override
    public void show() {
        setBackground("assets/images/backg/PVZIOS_newtitle.png");
        super.show();
        buildRegisterStep();
    }

    private void buildRegisterStep() {
        step = Step.REGISTER;
        rootTable.clear();

        TextField username = field(false);
        TextField password = field(true);
        TextField confirmPassword = field(true);
        TextField nickname = field(false);
        TextField email = field(false);

        TextButton male = new TextButton("Male", skin, "gender-button");
        TextButton female = new TextButton("Female", skin, "gender-button");
        male.setChecked(true);
        ButtonGroup<TextButton> genderGroup = new ButtonGroup<>();
        genderGroup.setMinCheckCount(1);
        genderGroup.setMaxCheckCount(1);
        genderGroup.add(male, female);

        Table fields = new Table();
        fields.defaults().space(SPACE_SM);
        addRow(fields, "Username", username);
        addRow(fields, "Password", password);
        addRow(fields, "Confirm password", confirmPassword);
        addRow(fields, "Nickname", nickname);
        addRow(fields, "Email", email);

        Table genderRow = new Table();
        genderRow.add(male).width(150).padRight(SPACE_SM);
        genderRow.add(female).width(150);
        fields.add(new Label("Gender", skin, "main")).width(LABEL_WIDTH).left();
        fields.add(genderRow).left().row();

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(CARD_PAD).defaults().space(SPACE_SM);
        addTitleImage(card);
        card.add(new Label("Create your account", skin, "title")).colspan(2).padBottom(SPACE_MD).row();
        card.add(scrollable(fields)).colspan(2).width(400).height(300).padBottom(SPACE_SM).row();

        card.add(primaryButton("Register", () -> {
            String gender = male.isChecked() ? "male" : "female";
            runCommand("register -u " + username.getText()
                    + " -p " + password.getText() + " " + confirmPassword.getText()
                    + " -n " + nickname.getText()
                    + " -e " + email.getText()
                    + " -g " + gender);
        })).colspan(2).width(BUTTON_WIDTH).row();

        card.add(secondaryButton("Already have an account? Log in", () -> runCommand("menu enter login")))
                .colspan(2).padTop(SPACE_SM).width(BUTTON_WIDTH).row();
        card.add(secondaryButton("Quit", this::confirmQuit)).colspan(2).width(BUTTON_WIDTH).row();

        rootTable.add(card);
    }

    private void buildSecurityQuestionStep() {
        step = Step.SECURITY_QUESTION;
        rootTable.clear();

        List<String> questions = SignupMenu.getSecurityQuestions();
        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);

        Table content = new Table();
        content.defaults().space(SPACE_SM);
        for (String q : questions) {
            TextButton questionButton = new TextButton(q, skin, "main");
            questionButton.getLabel().setWrap(true);
            group.add(questionButton);
            content.add(questionButton).width(400).left().row();
        }
        if (!group.getButtons().isEmpty()) {
            group.getButtons().first().setChecked(true);
        }

        TextField answer = field(false);
        TextField confirmAnswer = field(false);
        content.row();
        addRow(content, "Answer", answer);
        addRow(content, "Confirm answer", confirmAnswer);

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(CARD_PAD).defaults().space(SPACE_SM);
        addTitleImage(card);
        card.add(new Label("Pick a security question", skin, "title")).colspan(2).padBottom(SPACE_MD).row();
        card.add(scrollable(content)).colspan(2).width(420).height(320).padBottom(SPACE_SM).row();

        card.add(primaryButton("Submit", () -> {
            int questionNumber = group.getCheckedIndex() + 1;
            runCommand("pick question -q " + questionNumber + " -a " + answer.getText() + " -c " + confirmAnswer.getText());
        })).colspan(2).width(BUTTON_WIDTH).row();

        rootTable.add(card);
    }

    private void confirmQuit() {
        new ConfirmModal("Quit Plants vs. Zombies?", "Any unsaved progress will be lost.", "Quit",
                () -> runCommand("menu exit")).show();
    }

    @Override
    protected void onAfterCommand() {
        boolean pendingQuestion = SignupMenu.isPendingSecurityAnswer();
        if (pendingQuestion && step != Step.SECURITY_QUESTION) {
            buildSecurityQuestionStep();
        } else if (!pendingQuestion && step == Step.SECURITY_QUESTION) {
            buildRegisterStep();
        }
    }
}