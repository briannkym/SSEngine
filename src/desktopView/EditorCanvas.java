package desktopView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class EditorCanvas extends JFrame implements IDesktopCanvas{
	
	private static final long serialVersionUID = 1L;

	public static final int TOP_SPACE = 40;
	private int width, height, x, y, xOffset, yOffset;
	JPanel jp;
	BufferedImage bi;
	Graphics buffer;
	private Graphics g;
	
	public EditorCanvas(int width, int height, int xOffset, int yOffset, String title) {
		super(title);
		this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer = bi.getGraphics();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void paint() {
		try {
			g = jp.getGraphics();
			if ((g != null) && (bi != null)) {
				g.drawImage(bi, x, y, width, height, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();

				buffer.setColor(new Color(0xFFFFFFFF));
				buffer.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fullScreen() {
	}

	@Override
	public void windowScreen() {
		Container c = this.getContentPane();
		jp = new JPanel();
		jp.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
		this.x = 0;
		this.y = 0;
		this.width = bi.getWidth();
		this.height = bi.getHeight();
		g = jp.getGraphics();
		c.setLayout(new BorderLayout());
		c.add(jp, BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIgnoreRepaint(true);
		this.pack();
		this.setLocation(xOffset, yOffset);
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void register() {
	}

	@Override
	public void drawImage(BufferedImage bi, int x, int y) {
		buffer.drawImage(bi, x, y, null);
	}

	@Override
	public void setRotation(int degrees) {
		// TODO Auto-generated method stub
		
	}

}
