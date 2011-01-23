package edu.zzu.leon;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8718723328778502530L;
	private int x, y;
	private int imagecenter_x, imagecenter_y;
	private ImageIcon imageIcon;
	private ImageObserver imageObserver;
	private double ZOOM = 1;
	private Image image;
	private String imageFilePath;
	private BufferedImage srcImage;
	private AffineTransform affineTransform;
	private static int count = 0;
	int opIndex;
	int lastOp;
	private BufferedImage biFiltered;

    public static final float[] SHARPEN3x3 = { // sharpening filter kernel
        0.f, -1.f,  0.f,
       -1.f,  5.f, -1.f,
        0.f, -1.f,  0.f
    };

    public static final float[] BLUR3x3 = {
        0.1f, 0.1f, 0.1f,    // low-pass filter kernel
        0.1f, 0.2f, 0.1f,
        0.1f, 0.1f, 0.1f
    };
	
	public ImagePanel() {
		this.affineTransform = new AffineTransform();
	}

	public void moveX(int x_add) {
		this.imagecenter_x = imagecenter_x + x_add;
	}

	public void moveY(int y_add) {
		this.imagecenter_y = imagecenter_y + y_add;
	}

//	 public void open(){
//		 JFileChooser jfc = new JFileChooser(new File("./src"));
//		 System.out.println("open open");
//		 if(jfc.showOpenDialog(null) == JFileChooser.OPEN_DIALOG){
//			 imageFilePath = jfc.getSelectedFile().getAbsolutePath();
//			 this.reset();
//		 }
//	 }

	public void setImagePath(String path) {
		this.imageFilePath = path;
	}

	// public void paint(Graphics g) {
	// if(image == null) return;
	// g.clearRect(0, 0, this.getWidth(), this.getHeight());
	// g.drawImage(image, x,
	// y,image.getWidth(imageObserver),image.getHeight(imageObserver),imageObserver);
	// ImageMain.jLabel.setText("ÏñËØ: "+image.getWidth(imageObserver)+" * "+image.getHeight(imageObserver));
	// }

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(imagecenter_x, imagecenter_y);
		if (image == null)
			return;
		g2.transform(affineTransform);
		// g2.drawi
		g2.drawImage(image, x, y, imageObserver);
		ImageFrame.jLabel.setText("ÏñËØ: " + image.getWidth(imageObserver) + " * "
				+ image.getHeight(imageObserver));

	}

	public void zoom_in() {
		ZOOM = 0.8;
		this.zoom();
	}

	public void zoom_out() {
		ZOOM = 1.25;
		this.zoom();
	}

	public void zoom() {
		if (srcImage == null)
			return;
		AffineTransform transform = AffineTransform
				.getScaleInstance(ZOOM, ZOOM);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_BICUBIC);
		srcImage = op.filter(srcImage, null);
		image = new ImageIcon(srcImage).getImage();
		this.x = -image.getWidth(imageObserver) / 2;
		this.y = -image.getHeight(imageObserver) / 2;
		this.repaint();
	}

	public void setRightRotate() {
		count++;
		this.setRotate();
	}

	public void setLeftRotate() {
		count--;
		this.setRotate();
	}

	public void setRotate() {
		System.out.println("count  :" + count);
		affineTransform.setToRotation(Math.toRadians(90 * count));
		this.repaint();
	}

	public void setImageEffects(int cbIndex) {
		opIndex = cbIndex;
		filterImage();
		image = new ImageIcon(biFiltered).getImage();
		this.repaint();
	}

	public void setBufferImage() {
		if (srcImage.getType() != BufferedImage.TYPE_INT_RGB) {
			BufferedImage bi2 = new BufferedImage(srcImage.getWidth(),
					srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics big = bi2.getGraphics();
			big.drawImage(srcImage, 0, 0, null);
			biFiltered = srcImage = bi2;
		}
	}

	public void filterImage() {
		BufferedImageOp op = null;

		if (opIndex == lastOp) {
			return;
		}
		lastOp = opIndex;
		switch (opIndex) {

		case 0:
			biFiltered = srcImage; /* original */
			return;
		case 1: /* low pass filter */
		case 2: /* sharpen */
			float[] data = (opIndex == 1) ? BLUR3x3 : SHARPEN3x3;
			op = new ConvolveOp(new Kernel(3, 3, data), ConvolveOp.EDGE_NO_OP,
					null);

			break;

		case 3: /* lookup */
			byte lut[] = new byte[256];
			for (int j = 0; j < 256; j++) {
				lut[j] = (byte) (256 - j);
			}
			ByteLookupTable blut = new ByteLookupTable(0, lut);
			op = new LookupOp(blut, null);
			break;
		}

		/*
		 * Rather than directly drawing the filtered image to the destination,
		 * filter it into a new image first, then that filtered image is ready
		 * for writing out or painting.
		 */
		biFiltered = new BufferedImage(srcImage.getWidth(),
				srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		op.filter(srcImage, biFiltered);
	}

	public void saveImage(String cbItemstr){
		String format = cbItemstr;
		File saveFile =new File("Î´ÃüÃû."+format);
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(saveFile);
		if(chooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			saveFile = chooser.getSelectedFile();
			try {
				ImageIO.write(biFiltered, format, saveFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void reset() {
		try {
			srcImage = ImageIO.read(new File(imageFilePath));
			this.setBufferImage();
		} catch (IOException e) {
			System.out.println("can not read the image");
			e.printStackTrace();
		}
		imageIcon = new ImageIcon(srcImage);
		image = imageIcon.getImage();
		imageObserver = imageIcon.getImageObserver();
		imagecenter_x = this.getWidth() / 2;
		imagecenter_y = this.getHeight() / 2;
		this.x = -image.getWidth(imageObserver) / 2;
		this.y = -image.getHeight(imageObserver) / 2;
		affineTransform.setToRotation(0);
		this.repaint();
	}
}
