import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;

public class GUI extends JFrame {
    private ArrayList<Integer> generatedRoute;


    public GUI(ArrayList<Node> allNodes, int[][] adjMatrix, String[] allNodeNames, String AREA_NAME) {
        setTitle("Route Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(null);
        setBounds(50, 50, 800, 700);

        JLabel fieldCaption = new JLabel("Enter your start location: ");
        fieldCaption.setBounds(10, 10, 200, 30);

        JTextField startEntry = new JTextField();
        startEntry.setBounds(160, 10, 120, 30);

        JLabel destinationCaption = new JLabel("Enter your destination: ");
        destinationCaption.setBounds(290, 10, 200, 30);

        JTextField destinationEntry = new JTextField();
        destinationEntry.setBounds(430, 10, 120, 30);

        //Calculate Route | Print
        JButton goButton = new JButton("Calculate Route");
        goButton.setBounds(560, 10, 140, 30);

        JButton printButton = new JButton("Print");
        printButton.setBounds(710,10,70,30);

        JPanel panel = new JPanel();
        panel.setBackground(Color.lightGray);
        panel.setBounds(0, 0, 800, 50);

        //Dont add the canvas until after
        //Draw Nodes then draw route once it has been entered
        Canvas backgroundMap = new Canvas(allNodes, adjMatrix, AREA_NAME);
        backgroundMap.setBounds(10, 50, 800, 650);
        backgroundMap.setOpaque(true);

        RouteCanvas routeDrawing = new RouteCanvas(generatedRoute, allNodes, AREA_NAME);
        routeDrawing.setBounds(10, 50, 800, 650);
        routeDrawing.setBackground(Color.white);
        routeDrawing.setOpaque(false);

        //adds a box for displaying the length of the route
        JPanel distanceBox = new JPanel();
        distanceBox.setBackground(Color.LIGHT_GRAY);
        distanceBox.setBounds(700, 50, 90,30);
        distanceBox.setVisible(false);

        add(fieldCaption);
        add(startEntry);
        add(destinationCaption);
        add(destinationEntry);
        add(backgroundMap);
        add(routeDrawing);
        add(panel);
        add(printButton);
        add(goButton);
        add(distanceBox);
        backgroundMap.setVisible(true);

        setVisible(true);
        routeDrawing.setVisible(false);

        goButton.addActionListener(e -> {
            //this takes the input from the text fields and turns them to a digit
            String start = startEntry.getText();
            String destination = destinationEntry.getText();

            int indexOfStart = getIndexOfRoadName(start, allNodeNames);
            int indexOfDestination = getIndexOfRoadName(destination, allNodeNames);

            //one of the inputs isnt contained in the array of names
            if (indexOfStart == -1 || indexOfDestination == -1){
                createWrongRoadWindow();


            } else { //both of the names are valid, and associated with existing nodes
                MainLoopOfGA newGA = new MainLoopOfGA(indexOfStart, indexOfDestination, allNodes, adjMatrix);
                ArrayList<Integer> bestRoute = newGA.getBestRoute();
                generatedRoute = bestRoute;

                //z-order is determined by order of painting,
                // so in order to draw route on top of map, have to repaint it over
                backgroundMap.setVisible(false);

                routeDrawing.setRoute(bestRoute);
                routeDrawing.setVisible(true);
                routeDrawing.repaint();

                backgroundMap.repaint();
                backgroundMap.setVisible(true);

                //for displaying the distance of the route
                // roughly converts into miles
                String distance = String.format("%.2f",(newGA.getBestRouteLength()/0.02));
                JLabel distanceToDraw = new JLabel(distance+" miles");
                distanceBox.removeAll();
                distanceBox.add(distanceToDraw);
                distanceBox.setVisible(true);
                distanceBox.repaint();
                getContentPane().setComponentZOrder(distanceBox, 0);
                //distanceBox.getComponent(0).repaint();
            }

            });

        printButton.addActionListener(f -> {
            try {
                BufferedImage screenshot = getScreenshotOfBothCanvases(backgroundMap, routeDrawing);
                // write the image as a PNG
                ImageIO.write(
                        screenshot,
                        "png",
                        new File("screenshot.png"));


                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(new Printing(screenshot));
                boolean doPrint = job.printDialog();

                if (doPrint) {
                    try {
                        job.print();
                    } catch (PrinterException exc) {
                        // The job did not successfully complete
                        createUnableToScreenshotWindow();
                    }
                }
            } catch (Exception err) {
                createUnableToScreenshotWindow();
            }

        });
        getContentPane().setComponentZOrder(goButton, 0);
        getContentPane().setComponentZOrder(printButton,0);

    }

    //a road name inputted isn't in the data
    private void createWrongRoadWindow() {
        JFrame frame = new JFrame("Road Name Error");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createWrongRoadUI(frame);
        frame.setSize(450, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createWrongRoadUI(final JFrame frame) {
        JLabel warningMessage = new JLabel("At least one of these roads isn't included in the database, please try again.");
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private void createUnableToScreenshotWindow() {
        JFrame frame = new JFrame("Screenshot Error");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUnableToScreenshotUI(frame);
        frame.setSize(450, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createUnableToScreenshotUI(final JFrame frame) {
        JLabel warningMessage = new JLabel("Currently unable to print, please close and reopen the program.");
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private BufferedImage getScreenshotOfBothCanvases(Component component1, Component component2) {
        int width = Math.max(component1.getWidth(), component2.getWidth());
        int height = Math.max(component1.getHeight(), component2.getHeight());

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combined.createGraphics();

        //set white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);


        // paint route on top if it exists
        if (generatedRoute != null) {
            component2.printAll(g2d);
        }

        //paint background first, then generated route
        component1.printAll(g2d);

        g2d.dispose();
        return combined;
    }

    //returns index or otherwise returns -1
    // will return the first instance of a roadName
    private int getIndexOfRoadName(String roadName, String[] arrayOfRoadNames){
        boolean isFound = false;
        int count = 0;

        while (count < arrayOfRoadNames.length && !isFound){
            if (arrayOfRoadNames[count].equals(roadName)){
                isFound = true;
            }else {
                count++;
            }
        }

        if (count == arrayOfRoadNames.length){
            count = -1;
        }

        return count;
    }



}

