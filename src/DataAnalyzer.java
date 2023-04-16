import java.io.*;

import util.ColorOps;
import util.SVGWriter;

public class DataAnalyzer {
    public static void main(String[] args) throws Exception {
        analyze("M2I");
        analyze("I2M");
    }

    public static void analyze(String type) throws Exception {
        String fileName = (type.equalsIgnoreCase("M2I")) ? "assets/data/responseM2I.csv" : "assets/data/responseI2M.csv";
        File csv = new File(fileName);

        // count lines in csv
        long lines = 0;
        try (LineNumberReader lnr = new LineNumberReader(new FileReader(csv))) {
            while (lnr.readLine() != null);
            lines = lnr.getLineNumber();
        } catch (IOException e) {}

        double height = 495 * Math.ceil((lines - 1) / 15.0) + 25;

        File file = new File("assets/data/figures" + type + ".svg");
        SVGWriter writer = new SVGWriter(file, 0, 0, 9550, height);

        // for each line in csv
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line = br.readLine();
            long lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // split line into array
                String[] values = line.split(",");

                writer.setFill("#ffffff7f");
                writer.writeRect(25 + 635 * ((lineNumber - 2) % 15), 25 + 495 * ((lineNumber - 2) / 15), 610, 470);
                
                String path;
                float color[];
                if (type.equalsIgnoreCase("M2I")) {
                    path = shiftPath(values[11], 635 * ((lineNumber - 2) % 15), 495 * ((lineNumber - 2) / 15) - 25);
                    color = new float[]{Float.parseFloat(values[8]), 100 * Float.parseFloat(values[9]), 100 * Float.parseFloat(values[10])};
                } else {
                    path = shiftPath(values[6], 635 * ((lineNumber - 2) % 15) + 25, 495 * ((lineNumber - 2) / 15) - 25 + 25);
                    color = new float[]{Float.parseFloat(values[3]), 100 * Float.parseFloat(values[4]), 100 * Float.parseFloat(values[5])};
                }
                
                String fillColor = Integer.toHexString(ColorOps.hsvToHex(color));
                while (fillColor.length() < 6) {
                    fillColor = "0" + fillColor;
                }
                writer.setFill("#" + fillColor);
                writer.writePath(path);
            }
        } catch (IOException e) {}

        writer.close();
    }

    public static String shiftPath(String path, double x, double y) {
        String newPath = "";
        boolean isX = true;

        // add a space before and after each letter
        path = path.replaceAll("([A-Za-z])", " $1 ");
        
        // remove extra spaces and double spaces
        path = path.replaceAll(" +", " ");
        path = path.trim();

        String[] components = path.split(" ");
        for (String component : components) {
            if (component.matches("[A-Za-z]")) {
                newPath += component;
            } else {
                if (isX) {
                    newPath += (Double.parseDouble(component) + x);
                } else {
                    newPath += (Double.parseDouble(component) + y);
                }
                isX = !isX;
            }
            newPath += " ";
        }
        return newPath;
    }
}
