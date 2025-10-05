package view;

import controller.Controller;
import database.dto.MatchDto;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.OcrScanner;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CarteIdentiteController {

    @FXML
    private Button importButton;
    @FXML
    private Button cropButton;
    @FXML
    private VBox thumbnailContainer;
    @FXML
    private VBox croppedImagesContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private Canvas cropCanvas;
    @FXML
    private Button finishButton;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Button backButton;

    private final List<File> importedImages = new ArrayList<>();
    private final Map<File, Boolean> croppedStatus = new HashMap<>();
    private final List<BufferedImage> croppedImages = new ArrayList<>();

    private double startX, startY, endX, endY;
    private Image currentImage;
    private File currentImageFile;
    private MatchDto match;
    private int equipeId;
    private Stage stage;
    private Controller appController;


    /**
     * Sets the match and team ID for the controller.
     * Initializes the OCR scanner for the selected match and team.
     *
     * @param match    the MatchDto object.
     * @param equipeId the team identifier.
     */
    public void setMatch(MatchDto match, int equipeId) {
        this.match = match;
        this.equipeId = equipeId;
        appController.initialiserOcrPourMatch(match, equipeId);
    }

    /**
     * Sets the stage (window) reference for navigation.
     *
     * @param stage the JavaFX Stage object.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the main app controller reference.
     *
     * @param appController the Controller instance.
     */
    public void setAppController(Controller appController) {
        this.appController = appController;

    }

    @FXML
    public void initialize() {
        OcrScanner.clearScan();
        importButton.setOnAction(e -> importImages());
        cropButton.setOnAction(e -> cropImage());
        finishButton.setOnAction(e -> {
            loadingIndicator.setVisible(true);

            appController.lancerAnalyseCartes(croppedImages, () -> {
                loadingIndicator.setVisible(false);
                ouvrirVueFiches();
            });
        });
        finishButton.setDisable(true);
        backButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detailsMatch.fxml"));
                Parent root = loader.load();
                DetailsMatchController controller = loader.getController();
                controller.setAppController(appController);
                controller.setMatch(match);

                if (stage != null) {
                    controller.setStage(stage);
                    stage.setScene(new Scene(root));
                    stage.setTitle("RBFA - Détails du match");
                    stage.centerOnScreen();
                } else {
                    Stage newStage = new Stage();
                    controller.setStage(newStage);
                    newStage.setScene(new Scene(root));
                    newStage.setTitle("RBFA - Détails du match");
                    newStage.centerOnScreen();
                    newStage.show();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        cropButton.setDisable(true);
        loadingIndicator.setVisible(false);
        setupCroppingTool();
    }

    /**
     * Opens the profile view after finishing the image processing.
     */
    private void ouvrirVueFiches() {
        try {
            OcrScanner.setCurrentMatch(match);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profilPersonne.fxml"));
            Parent root = loader.load();
            ProfilPersonneController controller = loader.getController();
            controller.setAppController(appController);
            controller.setMatch(match,equipeId);

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("Profil Personne");
                stage.centerOnScreen();
            } else {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Profil Personne");
                newStage.centerOnScreen();
                newStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a file chooser dialog to import images and adds them to the thumbnail container.
     */
    private void importImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir des images");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            if (importedImages.size() + files.size() > 15) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Limite atteinte");
                alert.setHeaderText("Limite de cartes atteinte");
                alert.setContentText("Vous ne pouvez pas importer plus de 15 cartes par équipe.");
                alert.showAndWait();
                return;
            }

            importedImages.addAll(files);
            for (File f : files) {
                croppedStatus.put(f, false);
                HBox thumbnailBox = createThumbnailWithIcon(f);
                thumbnailContainer.getChildren().add(thumbnailBox);
            }
        }
    }

    /**
     * Creates a thumbnail component with a status icon for a given image file.
     *
     * @param file the image file.
     * @return an HBox containing the thumbnail and status icon.
     */
    private HBox createThumbnailWithIcon(File file) {
        Image image = new Image(file.toURI().toString(), 130, 130, true, true);
        ImageView thumbnail = new ImageView(image);
        thumbnail.setFitWidth(130);
        thumbnail.setPreserveRatio(true);
        thumbnail.setOnMouseClicked(e -> {
            loadImage(file);
            currentImageFile = file;
        });

        Circle statusIcon = new Circle(8);
        updateStatusIconColor(file, statusIcon);

        HBox box = new HBox(10, thumbnail, statusIcon);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /**
     * Updates the color of the status icon for a given file based on cropping status.
     *
     * @param file the image file.
     * @param icon the Circle icon to update.
     */
    private void updateStatusIconColor(File file, Circle icon) {
        icon.setFill(croppedStatus.get(file) ? Color.GREEN : Color.RED);
    }

    /**
     * Loads the selected image into the main image view and prepares the crop canvas.
     *
     * @param file the image file to load.
     */
    private void loadImage(File file) {
        currentImage = new Image(file.toURI().toString());
        imageView.setImage(currentImage);
        cropCanvas.setWidth(600);
        cropCanvas.setHeight(400);
        clearCanvas();
        cropButton.setDisable(croppedStatus.get(file));
    }


    /**
     * Sets up the mouse handlers on the crop canvas to allow drawing crop rectangles.
     */
    private void setupCroppingTool() {
        GraphicsContext gc = cropCanvas.getGraphicsContext2D();

        cropCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            startX = e.getX();
            startY = e.getY();
            clearCanvas();
        });

        cropCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            endX = e.getX();
            endY = e.getY();
            clearCanvas();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeRect(startX, startY, endX - startX, endY - startY);
        });
    }

    /**
     * Clears the crop canvas by wiping any drawn shapes.
     */
    private void clearCanvas() {
        cropCanvas.getGraphicsContext2D().clearRect(0, 0, cropCanvas.getWidth(), cropCanvas.getHeight());
    }

    /**
     * Crops the currently loaded image based on user selection and updates the UI.
     * Also updates the cropped image list and disables further cropping on the same image.
     */
    private void cropImage() {
        if (currentImage == null || currentImageFile == null || croppedStatus.get(currentImageFile)) return;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de rognage");
        confirmAlert.setHeaderText("Voulez-vous vraiment rogner cette image ?");
        confirmAlert.setContentText("Le rognage sera définitif pour cette image.");


        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;-fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        ButtonType buttonTypeOk = confirmAlert.getButtonTypes().getFirst();
        Button okButton = (Button) dialogPane.lookupButton(buttonTypeOk);
        okButton.setStyle("-fx-background-color: #B39B63; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(179,155,99,0.3), 10, 0, 0, 2);");

        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #B39B63; -fx-font-size: 16px; -fx-font-weight: bold;");


        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        double cropStartX = Math.min(startX, endX);
        double cropStartY = Math.min(startY, endY);
        double cropWidth = Math.abs(endX - startX);
        double cropHeight = Math.abs(endY - startY);

        double imageViewWidth = imageView.getFitWidth();
        double imageViewHeight = imageView.getFitHeight();
        double imageRatio = currentImage.getWidth() / currentImage.getHeight();

        double displayedWidth, displayedHeight;
        if (imageRatio > imageViewWidth / imageViewHeight) {
            displayedWidth = imageViewWidth;
            displayedHeight = displayedWidth / imageRatio;
        } else {
            displayedHeight = imageViewHeight;
            displayedWidth = displayedHeight * imageRatio;
        }

        double offsetX = (imageViewWidth - displayedWidth) / 2;
        double offsetY = (imageViewHeight - displayedHeight) / 2;

        cropStartX -= offsetX;
        cropStartY -= offsetY;

        double scaleX = currentImage.getWidth() / displayedWidth;
        double scaleY = currentImage.getHeight() / displayedHeight;

        int realX = (int) (cropStartX * scaleX);
        int realY = (int) (cropStartY * scaleY);
        int realWidth = (int) (cropWidth * scaleX);
        int realHeight = (int) (cropHeight * scaleY);

        realX = Math.max(0, Math.min(realX, (int) currentImage.getWidth() - 1));
        realY = Math.max(0, Math.min(realY, (int) currentImage.getHeight() - 1));
        realWidth = Math.min(realWidth, (int) currentImage.getWidth() - realX);
        realHeight = Math.min(realHeight, (int) currentImage.getHeight() - realY);

        if (realWidth <= 0 || realHeight <= 0) return;

        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(currentImage, null);
            BufferedImage croppedImage = bufferedImage.getSubimage(realX, realY, realWidth, realHeight);
            croppedImages.add(croppedImage);
            Image fxCroppedImage = SwingFXUtils.toFXImage(croppedImage, null);

            ImageView croppedView = new ImageView(fxCroppedImage);
            croppedView.setFitWidth(160);
            croppedView.setFitHeight(120);
            croppedView.setPreserveRatio(true);
            croppedView.setSmooth(true);
            croppedView.setStyle("-fx-border-color: #ccc; -fx-border-radius: 4px;");

            HBox croppedBox = new HBox(croppedView);
            croppedBox.setAlignment(Pos.CENTER);
            croppedBox.setStyle("-fx-background-color: #ffffff;");

            croppedImagesContainer.getChildren().addFirst(croppedBox);
            imageView.setImage(fxCroppedImage);

            croppedStatus.put(currentImageFile, true);
            cropButton.setDisable(true);
            refreshThumbnails();

        } catch (RasterFormatException e) {
            System.out.println("Erreur lors du rognage : " + e.getMessage());
        }

        clearCanvas();
        finishButton.setDisable(false);
    }

    /**
     * Refreshes the thumbnail container to reflect updated cropping statuses.
     */
    private void refreshThumbnails() {
        thumbnailContainer.getChildren().clear();
        for (File file : importedImages) {
            thumbnailContainer.getChildren().add(createThumbnailWithIcon(file));
        }
    }
}