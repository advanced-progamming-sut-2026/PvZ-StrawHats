package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import controller.menus.authentication.LoginMenu;

/**
 * Graphical version of {@link LoginMenu}, including its "forgot password" sub-flow. Each step
 * formats the same command string the console version expected ({@code Regex.LOGIN},
 * {@code Regex.FORGET_PASSWORD}, {@code Regex.ANSWER}) and hands it to {@code runCommand()};
 * {@link LoginMenu#isAwaitingSecurityAnswer()} / {@link LoginMenu#isAwaitingNewPassword()}
 * (small additive getters, no behavior change) tell this screen which step to show next.
 */
public class LoginScreen extends AuthScreen {

    private enum Step { LOGIN, FORGOT_PASSWORD, SECURITY_ANSWER, NEW_PASSWORD }

    private Step step = Step.LOGIN;

    @Override
    public void show() {
        super.show();
        buildLoginStep();
    }

    private void buildLoginStep() {
        step = Step.LOGIN;
        rootTable.clear();

        TextField username = field(false);
        TextField password = field(true);
        CheckBox stayLoggedIn = new CheckBox(" Stay logged in", skin);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Welcome back", skin, "title")).colspan(2).padBottom(18).row();
        addRow(card, "Username", username);
        addRow(card, "Password", password);
        card.add(stayLoggedIn).colspan(2).left().padTop(4).row();

        card.add(primaryButton("Log in", () -> {
            String cmd = "login -u " + username.getText() + " -p " + password.getText();
            if (stayLoggedIn.isChecked()) {
                cmd += " -stay-logged-in";
            }
            runCommand(cmd);
        })).colspan(2).padTop(12).width(300).row();

        card.add(secondaryButton("Forgot password?", this::buildForgotPasswordStep)).colspan(2).padTop(10).row();
        // LoginMenu.exitMenu() -> changeMenu("SignUp Menu"); this is phase-1's only way back to Signup.
        card.add(secondaryButton("New here? Create an account", () -> runCommand("menu exit")))
                .colspan(2).padTop(4).row();

        rootTable.add(card);
    }

    private void buildForgotPasswordStep() {
        step = Step.FORGOT_PASSWORD;
        rootTable.clear();

        TextField username = field(false);
        TextField email = field(false);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Reset your password", skin, "title")).colspan(2).padBottom(18).row();
        addRow(card, "Username", username);
        addRow(card, "Email", email);

        card.add(primaryButton("Continue", () ->
                runCommand("forget password -u " + username.getText() + " -e " + email.getText())))
                .colspan(2).padTop(12).width(300).row();
        card.add(secondaryButton("Back to login", this::buildLoginStep)).colspan(2).padTop(10).row();

        rootTable.add(card);
    }

    private void buildSecurityAnswerStep() {
        step = Step.SECURITY_ANSWER;
        rootTable.clear();

        TextField answer = field(false);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Security question", skin, "title")).colspan(2).padBottom(12).row();

        Label question = new Label(String.valueOf(LoginMenu.getPendingSecurityQuestion()), skin, "muted");
        question.setWrap(true);
        card.add(question).colspan(2).width(440).padBottom(12).row();

        addRow(card, "Answer", answer);

        card.add(primaryButton("Submit", () -> runCommand("answer -a " + answer.getText())))
                .colspan(2).padTop(12).width(300).row();
        card.add(secondaryButton("Back to login", this::buildLoginStep)).colspan(2).padTop(10).row();

        rootTable.add(card);
    }

    private void buildNewPasswordStep() {
        step = Step.NEW_PASSWORD;
        rootTable.clear();

        TextField newPassword = field(true);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label("Choose a new password", skin, "title")).colspan(2).padBottom(18).row();
        addRow(card, "New password", newPassword);

        // LoginMenu.handleNewPassword() reads this as a raw line, not a "-p ..." flag - same as the console.
        card.add(primaryButton("Save password", () -> runCommand(newPassword.getText())))
                .colspan(2).padTop(12).width(300).row();

        rootTable.add(card);
    }

    @Override
    protected void onAfterCommand() {
        if (LoginMenu.isAwaitingNewPassword() && step != Step.NEW_PASSWORD) {
            buildNewPasswordStep();
        } else if (LoginMenu.isAwaitingSecurityAnswer() && step != Step.SECURITY_ANSWER) {
            buildSecurityAnswerStep();
        } else if (!LoginMenu.isAwaitingSecurityAnswer() && !LoginMenu.isAwaitingNewPassword()
                && (step == Step.SECURITY_ANSWER || step == Step.NEW_PASSWORD)) {
            // A wrong answer resets pendingPasswordReset server-side, and a saved password also
            // clears the wizard state - either way we belong back on the plain login form.
            buildLoginStep();
        }
    }
}
