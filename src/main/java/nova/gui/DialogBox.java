package nova.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * One message in the conversation: a block of text next to the avatar of
 * whoever said it.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image avatar) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(avatar);
    }

    /**
     * Mirrors the box so the avatar sits on the left, marking it as the other
     * side of the conversation.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a box for something the user said, avatar on the right.
     *
     * @param text   what the user typed
     * @param avatar the user's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text, Image avatar) {
        return new DialogBox(text, avatar);
    }

    /**
     * Creates a box for something the chatbot said, avatar on the left.
     *
     * @param text   the chatbot's reply
     * @param avatar the chatbot's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getNovaDialog(String text, Image avatar) {
        DialogBox box = new DialogBox(text, avatar);
        box.flip();
        return box;
    }
}
