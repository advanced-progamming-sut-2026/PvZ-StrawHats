package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import model.user_data.User;

public class ProfileMenuScreen extends AuthScreen {

    @Override
    public void show() {
        setBackground("assets/images/backg/profilemenu_background.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        User user = User.currentUser;

        Table card = new Table();
        card.setBackground(createTranslucentCardDrawable());
        card.pad(25).defaults().pad(6);

        if (user == null) {
            card.add(new Label("No user is currently logged in.", skin, "muted"));
            rootTable.add(card);
            return;
        }

        Table mainContent = new Table();

        Table leftTable = new Table();
        leftTable.defaults().pad(6);

        Stack avatarStack = new Stack();

        // 1. Frame Background (لایه زیرین)
        Texture frameTex = loadTextureSafe("assets/images/ui/reward4_bg.png");
        Image frameImg = new Image(frameTex);

        // 2. Avatar Image (لایه رویی که روی قاب می‌افتد)
        String avatarPath = (user.profilePicture != null && !user.profilePicture.isEmpty())
                ? user.profilePicture
                : "assets/images/ui/avatar_luffy.png";

        Texture avatarTex = loadTextureSafe(avatarPath);
        Image avatarImg = new Image(avatarTex);

        Table avatarContainer = new Table();
        avatarContainer.add(avatarImg).size(85, 85); // سایز متناسب برای قرارگیری دقیق در مرکز قاب

        // ترتیب افزودن: ابتدا قاب، سپس تصویر آواتار
        avatarStack.add(frameImg);
        avatarStack.add(avatarContainer);

        avatarStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAvatarSelectionPopup();
            }
        });

        leftTable.add(avatarStack).size(130, 130).padBottom(15).row();

        String statsText = "Games Played: " + user.userState.gamesPlayed + "\n"
                + "High Score: " + user.userState.highScore + "\n"
                + "Levels Passed: " + user.userState.lastLevel + "\n"
                + "Mini Games Won: " + user.userState.miniGamesWon + "\n"
                + "Quests Completed: " + user.userState.questsCompleted;

        Label statsLabel = new Label(statsText, skin, "title");
        statsLabel.setFontScale(0.85f);

        Table statsBox = new Table();
        statsBox.add(statsLabel).left();

        leftTable.add(statsBox).padTop(10).left().row();

        Table rightTable = new Table();
        rightTable.defaults().pad(6);

        TextField username = field(false);
        username.setText(user.username);
        addEditableRow(rightTable, "Username", username,
                () -> runCommand("menu profile change-username -u " + username.getText()));

        TextField nickname = field(false);
        nickname.setText(user.nickname);
        addEditableRow(rightTable, "Nickname", nickname,
                () -> runCommand("menu profile change-nickname -u " + nickname.getText()));

        TextField email = field(false);
        email.setText(user.email);
        addEditableRow(rightTable, "Email", email,
                () -> runCommand("menu profile change-email -e " + email.getText()));

        rightTable.add(new Label("Change Password", skin, "title")).colspan(2).padTop(15).padBottom(6).row();
        TextField oldPassword = field(true);
        TextField newPassword = field(true);
        addRow(rightTable, "Current Password", oldPassword);
        addRow(rightTable, "New Password", newPassword);

        rightTable.add(primaryButton("Update password", () ->
                        runCommand("menu profile change-password -p " + newPassword.getText()
                                + " -o " + oldPassword.getText())))
                .colspan(2).padTop(8).width(320).row();

        mainContent.add(leftTable).top().padRight(40);
        mainContent.add(rightTable).top();

        card.add(mainContent).row();

        card.add(secondaryButton("Back to Main Menu", () -> runCommand("menu exit")))
                .padTop(15).width(320).row();

        rootTable.add(card);
    }

    private void showAvatarSelectionPopup() {
        // ساخت پاپ‌آپ سفارشی بدون استفاده از Dialog جهت جلوگیری از کرش LibGDX
        final Table overlay = new Table();
        overlay.setFillParent(true);

        Pixmap dimPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        dimPixmap.setColor(0, 0, 0, 0.75f); // پس‌زمینه نیمه‌شفاف تاریک
        dimPixmap.fill();
        overlay.setBackground(new TextureRegionDrawable(new Texture(dimPixmap)));
        dimPixmap.dispose();

        Table popupCard = new Table();
        popupCard.setBackground(createTranslucentCardDrawable());
        popupCard.pad(20);

        popupCard.add(new Label("Select Avatar", skin, "title")).padBottom(15).row();

        Table grid = new Table();
        grid.defaults().pad(8);

        String[] avatars = new String[]{
                "assets/images/ui/avatar_luffy.png",
                "assets/images/ui/avatar_zoro.png",
                "assets/images/ui/avatar_nami.png",
                "assets/images/ui/avatar_usopp.png",
                "assets/images/ui/avatar_sanji.png",
                "assets/images/ui/avatar_chopper.png",
                "assets/images/ui/avatar_robin.png",
                "assets/images/ui/avatar_franky.png",
                "assets/images/ui/avatar_brook.png",
                "assets/images/ui/avatar_jinbe.png"
        };

        for (int i = 0; i < avatars.length; i++) {
            final String avatarPath = avatars[i];

            Stack itemStack = new Stack();

            Texture frameTex = loadTextureSafe("assets/images/ui/reward4_bg.png");
            Image frameImg = new Image(frameTex);

            Texture avTex = loadTextureSafe(avatarPath);
            Image avImg = new Image(avTex);

            Table imgContainer = new Table();
            imgContainer.add(avImg).size(55, 55);

            itemStack.add(frameImg);
            itemStack.add(imgContainer);

            itemStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (User.currentUser != null) {
                        User.currentUser.profilePicture = avatarPath;
                        User.save();
                    }
                    overlay.remove(); // بستن پاپ‌آپ
                    build(); // رفرش کردن صفحه برای نمایش عکس جدید
                }
            });

            grid.add(itemStack).size(75, 75);
            if ((i + 1) % 5 == 0) grid.row();
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setOverscroll(false, false);

        popupCard.add(scrollPane).width(450).height(200).row();
        popupCard.add(secondaryButton("Cancel", overlay::remove)).padTop(15).width(140);

        overlay.add(popupCard);

        // افزودن مستقیم پاپ‌آپ به stage که در AuthScreen تعریف شده است
        if (stage != null) {
            stage.addActor(overlay);
        }
    }

    private Texture loadTextureSafe(String path) {
        if (path != null && !path.isEmpty() && Gdx.files.internal(path).exists()) {
            Texture tex = new Texture(Gdx.files.internal(path));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return tex;
        }
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        Texture fallback = new Texture(pixmap);
        pixmap.dispose();
        return fallback;
    }

    private Drawable createTranslucentCardDrawable() {
        Pixmap pixmap = new Pixmap(780, 520, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.12f, 0.10f, 0.05f, 0.72f));
        pixmap.fill();
        pixmap.setColor(new Color(0.45f, 0.34f, 0.10f, 0.85f));
        pixmap.drawRectangle(0, 0, 780, 520);
        pixmap.drawRectangle(1, 1, 778, 518);
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private void addEditableRow(Table card, String labelText, TextField field, Runnable onSave) {
        card.add(new Label(labelText, skin, "main")).left();
        Table row = new Table();
        row.add(field).width(220).left();
        row.add(secondaryButton("Save", onSave)).padLeft(8);
        card.add(row).left().row();
    }

    @Override
    protected void onAfterCommand() {
        build();
    }
}