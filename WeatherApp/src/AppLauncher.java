import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //displaying content
                new WeatherAppGUI().setVisible(true);
            }

        });
    }
}
