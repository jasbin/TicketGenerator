package victory;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

public class Ticket {
	private static String QR_CODE_IMAGE_PATH = "D:\\tickets\\QR\\";
	private static String singleTicketDir = "D:\\tickets\\ticket\\";
	private JFrame frame;
	private static JTextField textEncode;
	private JTextField textDecode;
	private static int NUMBER_OF_TICKET = 1;

	public static void singleTicketGeneation() {
		try {
			if (DBQuery.insertRecord()) {
				int maxID = DBQuery.getMaxTicketID();
				generateQRCodeImage(maxID+"", 270, 270, maxID);
				OverlapImage(maxID);
			}
		} catch (Exception e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		}
	}
	public static void getMyWorkDone() {
		if(!textEncode.getText().isEmpty()) {
			NUMBER_OF_TICKET = Integer.parseInt(textEncode.getText());
			for(int i = NUMBER_OF_TICKET;i!=0; i--) {
				singleTicketGeneation();
			}
		}
		
	}

	
	// image overlap
	public static void OverlapImage(int maxID) {
		/**
		 * Read a background image
		 */
		String ticketBGPath = "D:\\STS\\newWorkspace\\victory\\image\\ticket.png";
		BufferedImage bgImage = readImage(ticketBGPath);

		/**
		 * Read a foreground image
		 */
		String QRPath = QR_CODE_IMAGE_PATH + maxID + ".png";
		BufferedImage fgImage = readImage(QRPath);

		/**
		 * Do the overlay of foreground image on background image
		 */
		BufferedImage overlayedImage = overlayImages(bgImage, fgImage);

		/**
		 * Write the overlayed image back to file
		 */
		if (overlayedImage != null) {
			String ticketPath = singleTicketDir + maxID + ".png";
			writeImage(overlayedImage, ticketPath, "JPG");
			System.out.println("Overlay Completed...");
		} else
			System.out.println("Problem With Overlay...");

	}

	public static BufferedImage overlayImages(BufferedImage bgImage, BufferedImage fgImage) {

		/**
		 * Doing some preliminary validations. Foreground image height cannot be greater
		 * than background image height. Foreground image width cannot be greater than
		 * background image width.
		 *
		 * returning a null value if such condition exists.
		 */
		if (fgImage.getHeight() > bgImage.getHeight() || fgImage.getWidth() > fgImage.getWidth()) {
			JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions"
					+ "nCannot proceed with overlay." + "nn Please use smaller Image for foreground");
			return null;
		}

		/** Create a Graphics from the background image **/
		Graphics2D g = bgImage.createGraphics();
		/** Set Antialias Rendering **/
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		/**
		 * Draw background image at location (0,0) You can change the (x,y) value as
		 * required
		 */
		g.drawImage(bgImage, 0, 0, null);

		/**
		 * Draw foreground image at location (0,0) Change (x,y) value as required.
		 */
		g.drawImage(fgImage, 1950, 375, null);

		g.dispose();
		return bgImage;
	}

	public static BufferedImage readImage(String fileLocation) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static void writeImage(BufferedImage img, String fileLocation, String extension) {
		try {
			BufferedImage bi = img;
			File outputfile = new File(fileLocation);
			ImageIO.write(bi, extension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// encode to QR Code
	private static Path generateQRCodeImage(String text, int width, int height, int maxID)
			throws WriterException, IOException {

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		String filePath = QR_CODE_IMAGE_PATH +maxID+".png";
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
		return path;
	}

	// decode to QR Code
	private static String decodeQRCode(File qrCodeimage) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}
	}
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBQuery.ticketDB();
					Ticket window = new Ticket();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the application.
	 */
	public Ticket() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JButton btnGen = new JButton("barcodeGen");
		btnGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getMyWorkDone();

			}
		});
		btnGen.setBounds(115, 407, 182, 56);
		panel.add(btnGen);

		JButton btnRead = new JButton("barcodeRead");
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File file = new File("D://MyQRCode.png");
					String decodedText = decodeQRCode(file);
					if (decodedText == null) {
						System.out.println("No QR Code found in the image");
					} else {
						textDecode.setText(decodedText);
						System.out.println("Decoded text = " + decodedText);
					}
				} catch (IOException e1) {
					System.out.println("Could not decode QR Code, IOException :: " + e1.getMessage());
				}
			}
		});
		btnRead.setBounds(406, 407, 182, 56);
		panel.add(btnRead);

		textEncode = new JTextField();
		textEncode.setBounds(110, 105, 478, 84);
		panel.add(textEncode);
		textEncode.setColumns(10);

		JLabel lblNewLabel = new JLabel("Number of TIckets");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setBounds(248, 48, 196, 52);
		panel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Decoded Text");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_1.setBounds(277, 218, 152, 31);
		panel.add(lblNewLabel_1);

		textDecode = new JTextField();
		textDecode.setBounds(110, 281, 478, 84);
		panel.add(textDecode);
		textDecode.setColumns(10);

		JButton ticketGen = new JButton("ticketGen");
		ticketGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// ticketgen
				// OverlapImage();
			}
		});
		ticketGen.setFont(new Font("Tahoma", Font.PLAIN, 17));
		ticketGen.setBounds(300, 489, 129, 25);
		panel.add(ticketGen);
	}
}
