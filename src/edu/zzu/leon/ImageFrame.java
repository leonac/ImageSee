package edu.zzu.leon;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ImageFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5627519538258271386L;
	private JMenuBar menuBar;
	private JMenu menu_file,menu_help;
	private JMenuItem menuItem_open,menuItem_exit,menuItem_about;
	private JToolBar toolBar;
	private JButton b_zoom_in,b_zoom_normal,b_zoom_out,b_leftroll,b_rightroll,b_openfiles;
	private JPanel eastPanel,east_northPanel;
	private ImagePanel imagePanel;
	private JScrollPane scrollPane;
	private JList list;
	public static JLabel jLabel;  //ImagePanel调用实时更改图片像素显示
	private int a_x,a_y,b_x,b_y;
	private JComboBox choices,formats;
	private String path; 
	private String descs[] = {
			    "Original", 
			        "Convolve : LowPass",
			        "Convolve : Sharpen", 
			        "LookupOp",
			    };
	
	
	public ImageFrame(){
		super();
		this.setTitle("ImageSee");
		this.setBounds(0,0,1024,768);
		this.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width/2-this.getWidth()/2,
				Toolkit.getDefaultToolkit().getScreenSize().height/2-this.getHeight()/2);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		menu_file = new JMenu("文件");
		menuBar.add(menu_file);
		
		menuItem_open = new JMenuItem("打开文件");
		menuItem_open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			//		imagePanel.open();
				 JFileChooser jfc = new JFileChooser(new File("./src"));
				 if(jfc.showOpenDialog(null) == JFileChooser.OPEN_DIALOG){
					 String imagePath = jfc.getSelectedFile().getAbsolutePath();
					 imagePanel.setImagePath(imagePath);
					 imagePanel.reset();
				 }
			}
		});
		menu_file.add(menuItem_open);

		menu_file.addSeparator();
		menuItem_exit = new JMenuItem("退出");
		menuItem_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu_file.add(menuItem_exit);
		
		menu_help = new JMenu();
		menu_help.setText("帮助");
		menuBar.add(menu_help);

		menuItem_about = new JMenuItem("关于");
		menuItem_about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,"author: leon\ne-mail: helloworld.leon@gmail.com","ImageSee 1.0",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu_help.add(menuItem_about);
		
		toolBar = new JToolBar();
		this.getContentPane().add(toolBar,BorderLayout.NORTH);
		b_zoom_in = new JButton("缩小");
		b_zoom_in.setIcon(SwingResourceManager.getIcon(ImageFrame.class,"/ICO/缩小.jpg"));
		b_zoom_in.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				imagePanel.zoom_in();
			}
			
		});
		//长按zoom_in键不放，是图片持续缩小如何实现
		toolBar.add(b_zoom_in);
		
		b_zoom_normal = new JButton("还原");
		b_zoom_normal.setIcon(SwingResourceManager.getIcon(ImageFrame.class,"/ICO/添加.jpg"));
		b_zoom_normal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				imagePanel.reset();
			}
		});
		toolBar.add(b_zoom_normal);
		
		b_zoom_out = new JButton("放大");
		b_zoom_out.setIcon(SwingResourceManager.getIcon(ImageFrame.class,"/ICO/放大.jpg"));
		b_zoom_out.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				imagePanel.zoom_out();
			}
		});
		toolBar.add(b_zoom_out);
		
		b_leftroll = new JButton("左翻转");
		b_leftroll.setIcon(SwingResourceManager.getIcon(ImageFrame.class,"/ICO/添加.jpg"));
		b_leftroll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("左翻转");
				imagePanel.setLeftRotate();
			}
			
		});
		toolBar.add(b_leftroll);
		
		b_rightroll = new JButton("右翻转");
		b_rightroll.setIcon(SwingResourceManager.getIcon(ImageFrame.class,"/ICO/添加.jpg"));
		b_rightroll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("右翻转");
				imagePanel.setRightRotate();
			}
			
		});
		toolBar.add(b_rightroll);
		
		toolBar.add(new JLabel("    风格       "));
		choices = new JComboBox(this.getDescriptions());
		choices.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				imagePanel.setImageEffects(choices.getSelectedIndex());
			}
			
		});
		
		toolBar.add(choices);
		toolBar.add(new JLabel("  另存为  "));
		formats = new JComboBox(this.getFormats());
		formats.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				imagePanel.saveImage(formats.getSelectedItem().toString());
			}
		});
		
		
		toolBar.add(formats);
		
		imagePanel = new ImagePanel();
		imagePanel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("mouse now at:("+e.getX()+","+e.getY()+")");
				b_x = e.getX();
				b_y = e.getY();
				imagePanel.moveX(b_x-a_x);
				imagePanel.moveY(b_y-a_y);
				imagePanel.repaint();
			}
			public void mousePressed(MouseEvent e) {
				a_x = e.getX();
				a_y = e.getY();
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
				
			}
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		imagePanel.addMouseWheelListener(new MouseWheelListener(){

			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				if(notches<0){
					System.out.println("Mouse wheel moved up");
					imagePanel.zoom_out();
				}else{
					System.out.println("Mouse wheel moved down");
					imagePanel.zoom_in();
				}
			}
			
		});
		
		this.getContentPane().add(imagePanel,BorderLayout.CENTER);
		
		eastPanel = new JPanel();
		this.getContentPane().add(eastPanel,BorderLayout.EAST);
		eastPanel.setLayout(new BorderLayout());
		
		east_northPanel = new JPanel();
		eastPanel.add(east_northPanel,BorderLayout.NORTH);
		
		toolBar = new JToolBar();
		b_openfiles = new JButton("打开文件夹");
		b_openfiles.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
//				 JFileChooser jfc = new JFileChooser(new File("./src"));
//				 if(jfc.showOpenDialog(null) == JFileChooser.DIRECTORIES_ONLY){
//					 File[] selectedFiles = jfc.getSelectedFiles();
//					 list.setListData(selectedFiles);
//					 for(int i=0;i<selectedFiles.length;i++){
//						 String filePath = selectedFiles[i].getAbsolutePath();
//					 }
//				 }
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("打开图片文件夹");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//chooser.setAcceptAllFileFilterUsed(false);  //设置只显示图片
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					 File selectedDir = chooser.getSelectedFile();
					 path = selectedDir.getAbsolutePath();
					 String[] subFiles = selectedDir.list();
					 list.removeAll();
					 list.setListData(subFiles);
				}
			}
		});
		toolBar.add(b_openfiles);
		east_northPanel.add(toolBar);
		
		scrollPane = new JScrollPane();
		eastPanel.add(scrollPane,BorderLayout.CENTER);
		list = new JList();
		list.setSelectedIndex(0);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String name = (String)list.getSelectedValue();
				imagePanel.setImagePath(path+File.separator+name);
				imagePanel.reset();
			}
		});
		scrollPane.getViewport().setView(list);
		
		toolBar = new JToolBar();
		jLabel = new JLabel("像素: "+imagePanel.getWidth()+" * "+imagePanel.getHeight());
		toolBar.add(jLabel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	public String[] getDescriptions(){
		return descs;
	}
	
    public String[] getFormats() {
        String[] formats = ImageIO.getWriterFormatNames();
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
	
	
	
	public static void main(String[] args) {
		new ImageFrame();
	}

}
