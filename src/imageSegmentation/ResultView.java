package imageSegmentation;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

import imageSegmentation.SelectiveSearchSegmentation.Region;

/**
 * 利用loadImage来更新显示的图片
 */
public class ResultView extends JFrame{

	protected static ExecutorService exec = Executors.newCachedThreadPool();
	/*
	 * 画板
	 */
	private GraphBasedImageSegmentation seg;
	private JPanelView jp;
	private static final long serialVersionUID = 1L;
	/*
	 * 存放其他组件
	 */
	private JPanel jpWin;

	ResultView(Image img,int k,int min){
		//设置
		seg = new GraphBasedImageSegmentation();
		//设置标题
		this.setTitle("Image");
		//设置组件的位置和大小(缩放一倍)
		this.setBounds(400, 400, img.getWidth(null) / 2, img.getHeight(null) / 2 + 100);
		//设置关闭窗口
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*
		 * JPanel实现初始时绘制图片
		 * 和paint(x,y)方法绘制方框
		 */
		this.jp = new JPanelView(img,this);
		jp.setSize(img.getWidth(null) / 2, img.getHeight(null) / 2);
		/*
		 * 设置
		 */
		this.add(jp);
		jp.setVisible(true);
		/*
		 * jpWin及其附件
		 */
		JTextField x = new JTextField(10);
		JTextField y = new JTextField(10);
		JLabel jlX = new JLabel("x:");
		JLabel jlY = new JLabel("y:");
		x.setText("0.0");
		y.setText("0.0");
		jpWin = new JPanel(new FlowLayout(2,10,10));
		jpWin.setSize(img.getWidth(null) / 2, 50);
		jpWin.setVisible(true);
		JButton startFirst = new JButton("startBase");
		JButton startSecond = new JButton("start");
		jpWin.add(jlX);
		jpWin.add(x);
		jpWin.add(jlY);
		jpWin.add(y);
		jpWin.add(startFirst);
		jpWin.add(startSecond);
		jlX.setVisible(true);
		jlY.setVisible(true);
		x.setVisible(true);
		y.setVisible(true);
		startFirst.setVisible(true);
		startSecond.setVisible(true);
		startFirst.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				/*
				 * 开始进行演示
				 */
				exec.execute(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("startBasic");
						Vector<Vector<Integer>> mask = seg.runBasic(img,k,min);
						if(mask == null || mask.isEmpty())return;
						Image img = createImage(mask.size(),mask.get(0).size());
						Graphics g = img.getGraphics();
						for(int x = 0;x < mask.size();x++) {
							for(int y = 0;y < mask.lastElement().size();y++) {
								//模拟画点
								g.setColor(new Color((int)(((mask.get(x).get(y) * 17) ^ 2) % 255),(int)((mask.get(x).get(y) ^ 2) % 255),(int)(mask.get(x).get(y) % 255)));
								g.drawLine(x, y, x, y + 1);
							}
						}	
						jp.loadImage(img);
						System.out.println("overBasic");
					}
				});
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {	
			}

			@Override
			public void mousePressed(MouseEvent arg0) {	
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
			
		});
		startSecond.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				/*
				 * 开始进行演示
				 */
				exec.execute(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("start");
						Vector<Region> regions = new SelectiveSearchSegmentation().runBasic(img,k,min);
						Image image = createImage(img.getWidth(null),img.getHeight(null));
						if(regions == null || regions.isEmpty())return;
						Graphics g = image.getGraphics();
						g.drawImage(img, 0, 0, img.getWidth(null),img.getHeight(null), null);
						for(Region region : regions) {
							if(region.size < SegmentationOptionsSetter.MINPOINT)continue;
							if((region.maxX - region.minX)/(region.maxY - region.minY) > SegmentationOptionsSetter.MAXPORTION || (region.maxY - region.minY)/(region.maxX - region.minX) > SegmentationOptionsSetter.MAXPORTION)continue;
							g.setColor(new Color(region.minX%255,region.minY%255,region.maxX%255));
							g.drawRect(region.minX, region.minY, region.maxX - region.minX,region.maxY - region.minY);
						}	
						jp.loadImage(image);
						System.out.println("over");
					}
				});
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {		
			}

			@Override
			public void mouseExited(MouseEvent e) {	
			}

			@Override
			public void mousePressed(MouseEvent e) {	
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
			}
			
		});
		jp.addMouseMotionListener(new MouseInputListener(){

			@Override
			public void mouseClicked(MouseEvent e) {	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
			}

			@Override
			public void mouseDragged(MouseEvent e) {	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				x.setText(String.valueOf(e.getX() * 2));
				y.setText(String.valueOf(e.getY() * 2));
			}
			
		});
		jp.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				x.setText(String.valueOf(e.getX() * 2));
				y.setText(String.valueOf(e.getY() * 2));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				x.setText("0");
				y.setText("0");
			}

			@Override
			public void mousePressed(MouseEvent e) {	
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		this.add("South",jpWin);
	}
	
	/*
	 * 利用loadImage来更新显示的图片
	 */
	protected class JPanelView extends JPanel{
		/**
		 * 显示图片
		 */
		private static final long serialVersionUID = 1L;
		private Image img;
		private ResultView parent;
		JPanelView(Image img,ResultView parent){
			this.img = img;
			this.parent = parent;
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(this.img == null)return;
            g.drawImage(this.img, 0, 0, getWidth(), getHeight(), null);
		}
		public void paintRect(int x,int y,int width,int height) {
			Graphics g = this.getGraphics();
			if (g == null) {
				return;
			}
			g.setColor(Color.RED);
			g.drawRect(x, y, width, height);
			super.update(g);
		}
		//实现图片的更新
		public void loadImage(Image img)
		{
			this.img=img;
			this.setSize(img.getWidth(null)/2, img.getHeight(null)/2);
			this.parent.setSize(img.getWidth(null)/2, img.getHeight(null)/2 + 100);
			this.repaint();
		}
	}
	
	public void paintRect(int x,int y,int width,int height) {
		this.jp.paintRect(x, y, width, height);
	}
	
	public void loadImage(Image img) {
		this.jp.loadImage(img);
	}
	
	public static void main(String[] args) {
		System.err.close();
		BufferedImage img;
		try {
			img = ImageIO.read(new File("testdata\\test05.jpg"));
			//BufferedImage anotherImg = ImageIO.read(new File("testdata\\test07.jpg"));
			exec.execute(new Runnable() {
				@Override
				public void run() {
					ResultView view = new ResultView(img,500,30);
					view.setVisible(true);	
					//view.loadImage(anotherImg);
				}
			});		
					} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
}
