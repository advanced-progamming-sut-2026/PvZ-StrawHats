package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import controller.menus.authentication.SignupMenu;

import java.util.List;

/**
 * Graphical version of {@link SignupMenu}. Buttons just format the same command strings the
 * console expected ({@code Regex.REGISTER}, {@code Regex.PICK_QUESTION}) and hand them to
 * {@code runCommand()} - every validation rule (username/password/nickname/email/gender, the
 * five fixed security questions) still lives only in {@code SignupMenu}.
 */
public class SignupScreen extends AuthScreen {

    private enum Step { REGISTER, SECURITY_QUESTION }

    private Step step = Step.REGISTER;

    @Override
    public void show() {
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

        TextButton male = new TextButton("Male", skin);
        TextButton female = new TextButton("Female", skin);
        male.setChecked(true);
        ButtonGroup<TextButton> genderGroup = new ButtonGroup<>();
        genderGroup.setMinCheckCount(1);
        genderGroup.setMaxCheckCount(1);
        genderGroup.add(male, female);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Create your account", skin, "title")).colspan(2).padBottom(18).row();
        addRow(card, "Username", username);
        addRow(card, "Password", password);
        addRow(card, "Confirm password", confirmPassword);
        addRow(card, "Nickname", nickname);
        addRow(card, "Email", email);

        Table genderRow = new Table();
        genderRow.add(male).pad(4).width(140);
        genderRow.add(female).pad(4).width(140);
        card.add(new Label("Gender", skin)).left();
        card.add(genderRow).left().row();

        card.add(primaryButton("Register", () -> {
            String gender = male.isChecked() ? "male" : "female";
            runCommand("register -u " + username.getText()
                    + " -p " + password.getText() + " " + confirmPassword.getText()
                    + " -n " + nickname.getText()
                    + " -e " + email.getText()
                    + " -g " + gender);
        })).colspan(2).padTop(12).width(300).row();

        card.add(secondaryButton("Already have an account? Log in", () -> runCommand("menu enter login")))
                .colspan(2).padTop(10).row();
        // SignupMenu.exitMenu() calls System.exit(0) directly - phase-1 behavior, gated behind a confirm here.
        card.add(secondaryButton("Quit", this::confirmQuit)).colspan(2).padTop(4).row();

        rootTable.add(card);
    }

    private void buildSecurityQuestionStep() {
        step = Step.SECURITY_QUESTION;
        rootTable.clear();

        List<String> questions = SignupMenu.getSecurityQuestions();
        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Pick a security question", skin, "title")).colspan(2).padBottom(18).row();

        for (String q : questions) {
            TextButton questionButton = new TextButton(q, skin);
            questionButton.getLabel().setWrap(true);
            group.add(questionButton);
            card.add(questionButton).colspan(2).width(440).left().row();
        }
        if (!group.getButtons().isEmpty()) {
            group.getButtons().first().setChecked(true);
        }

        TextField answer = field(false);
        TextField confirmAnswer = field(false);
        addRow(card, "Answer", answer);
        addRow(card, "Confirm answer", confirmAnswer);

        card.add(primaryButton("Submit", () -> {
            int questionNumber = group.getCheckedIndex() + 1;
            runCommand("pick question -q " + questionNumber + " -a " + answer.getText() + " -c " + confirmAnswer.getText());
        })).colspan(2).padTop(12).width(300).row();

        rootTable.add(card);
    }

    private void confirmQuit() {
        new ConfirmQuitModal(() -> runCommand("menu exit")).show();
    }

    @Override
    protected void onAfterCommand() {
        boolean pendingQuestion = SignupMenu.isPendingSecurityAnswer();
        if (pendingQuestion && step != Step.SECURITY_QUESTION) {
            buildSecurityQuestionStep();
        } else if (!pendingQuestion && step == Step.SECURITY_QUESTION) {
            // shouldn't normally happen (a correct pick moves to LoginScreen entirely), but keeps
            // this screen consistent if it's ever reached in a state we don't expect
            buildRegisterStep();
        }
    }
}
