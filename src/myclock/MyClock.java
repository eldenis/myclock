package myclock;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author dns.gonz at gmail.com
 */
public class MyClock extends javax.swing.JPanel implements Serializable {

    public enum Skins {

        cronometer,
        diner,
        flower,
        modern,
        modernblack,
        modernblue,
        moderncyan,
        moderngreen,
        modernmagenta,
        modernorange,
        square,
        system,
        trad,
        vatch1,
        vatch2
    }
    private static final Skins DEFAULT_SKIN = Skins.vatch2;
    private Skins skin = DEFAULT_SKIN;
    private int w = 130;
    private Point center = new Point(w / 2, w / 2);
    private Image imgSkin;
    private Image imgSecond;
    private Image imgMinute;
    private Image imgHour;
    private Image imgHighlight;

    /**
     * Creates a new instance of MyClock with the default Skin.
     */
    public MyClock() {
        this(DEFAULT_SKIN);
    }

    /**
     * Creates a new instance of MyClock with the skin selected.
     * @param skin  The skin to use.
     */
    public MyClock(Skins skin) {
        setBackground(new java.awt.Color(0, 0, 0));
        setPreferredSize(new java.awt.Dimension(130, 130));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());

        this.skin = skin;
        setImages();
        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    setPreviousSkin();
                } else {
                    setNextSkin();
                }
            }
        });
        Thread thread = new Thread(new Runnable() {

            public void run() {
                for (;;) {
                    paintClock();
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void setImages() {
        imgSkin = loadImage("images/" + this.skin + ".png");
        imgSecond = loadImage("images/" + this.skin + "_s.png");
        imgMinute = loadImage("images/" + this.skin + "_m.png");
        imgHour = loadImage("images/" + this.skin + "_h.png");
        imgHighlight = loadImage("images/" + this.skin + "_highlights.png");
    }

    public void setSkin(Skins skin) {
        this.skin = skin;
        setImages();
    }

    public Skins getSkin() {
        return skin;
    }

    public void setNextSkin() {
        switchSkin(1);
    }

    public void setPreviousSkin() {
        switchSkin(-1);
    }

    private void switchSkin(int increase) {
        int new_skin = this.skin.ordinal() + increase;
        Skins[] skins = Skins.values();
        new_skin = new_skin >= skins.length ? 0 : (new_skin < 0 ? skins.length - 1 : new_skin);
        this.skin = skins[new_skin];
        setImages();
    }

    private void paintClock() {
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(imgSkin, 0, 0, null);
        g2.drawImage(imgHighlight, 0, 0, null);

        AffineTransform origXform = g2.getTransform();

        double angles[] = getAngles();

        //drawing hour hand
        AffineTransform newXform = (AffineTransform) (origXform.clone());
        newXform.rotate(Math.toRadians(angles[2]), center.getX(), center.getY());
        g2.setTransform(newXform);
        int x = (w - imgHour.getWidth(this)) / 2;
        int y = (w - imgHour.getHeight(this)) / 2;
        g2.drawImage(imgHour, x, y, this);
        //end of drawing hour hand

        //drawing minute hand
        newXform = (AffineTransform) (origXform.clone());
        newXform.rotate(Math.toRadians(angles[1]), center.getX(), center.getY());
        g2.setTransform(newXform);
        x = (w - imgMinute.getWidth(this)) / 2;
        y = (w - imgMinute.getHeight(this)) / 2;
        g2.drawImage(imgMinute, x, y, this);
        //end of drawing minute hand

        //drawing second hand
        newXform = (AffineTransform) (origXform.clone());
        newXform.rotate(Math.toRadians(angles[0]), center.getX(), center.getY());
        g2.setTransform(newXform);
        x = (w - imgSecond.getWidth(this)) / 2;
        y = (w - imgSecond.getHeight(this)) / 2;
        g2.drawImage(imgSecond, x, y, this);
        //end of drawing second hand

        g2.setTransform(origXform);
    }

    private BufferedImage loadImage(String name) {
        BufferedImage bimg = null;
        try {
            URL url = getClass().getResource(name);
            if (url != null) {
                bimg = ImageIO.read(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bimg;
    }

    private double[] getAngles() {
        GregorianCalendar actTime = new GregorianCalendar();
        int ms = actTime.get(Calendar.MILLISECOND);
        double ss = actTime.get(Calendar.SECOND);
        double mm = actTime.get(Calendar.MINUTE);
        double hh = actTime.get(Calendar.HOUR);
        return new double[]{(360.0 / 600.0) * ((ss * 10) + (ms / 100)),
                    (360.0 / 3600.0) * ((mm * 60) + ss),
                    (360.0 / 43200.0) * (((hh) * 3600) + (mm * 60) + ss)
                };
    }

    public static void main(String args[]) {
        MyClock clock = new MyClock();
        clock.setVisible(true);

        JDialog frame = new JDialog((Frame) null, "MyClock", true);
        frame.setLocation(200, 200);
        frame.setResizable(false);
        frame.add(clock);
        frame.pack();
        frame.setVisible(true);
        JOptionPane.showMessageDialog(null,
                "                MyClock bean.\n" +
                "Created by dns.gonz@gmail.com\n" +
                "                    May 2010.");
        System.exit(0);
    }
}
