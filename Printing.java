import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

public class Printing implements Printable {
    PrinterJob job = PrinterJob.getPrinterJob();
    private BufferedImage screenshot;

    public Printing(BufferedImage screenshot){
        this.screenshot = screenshot;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // We have only one page, and 'page'
        // is zero-based
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        // User (0,0) is typically outside the
        // imageable area, so we must translate
        // by the X and Y values in the PageFormat
        // to avoid clipping.
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.rotate(Math.toRadians(90));
        g2d.translate(20,-screenshot.getHeight()+40);

        // Now we perform our rendering
        ((Graphics2D) g).drawImage(screenshot, null,0,0);

        // tell the caller that this page is part
        // of the printed document
        return PAGE_EXISTS;
    }

}





