package dk.dtu.compute.se.pisd.roborally.fileaccess;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader {
    public Image getImage(String imagePath){
        Image image = new Image(imagePath);
        return image;
    }
    public ImageView getImageView(String imagePath){
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        return imageView;
    }
}
