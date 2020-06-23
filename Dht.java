
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.fazecast.jSerialComm.SerialPort;

public class Dht {
	
	static SerialPort chosenPort;

	public static void main(String[] args) {
		
		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Sensor Graph GUI");
		window.setSize(600, 400);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create a drop-down box and connect button, then place them at the top of the window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);
		
		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		
		// configure the connect button and use another thread to listen for data
		connectButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				if(connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if(chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}
					
					// create a new thread that listens for incoming text and populates the graph
					Thread thread = new Thread(){
						@Override public void run() {
							 FileWriter file1;
							try {
								file1 = new FileWriter("C:\\xampp\\htdocs\\Arduino\\public_html\\dht.csv");
								file1.write("humS,tempS,light"+"\n");
								file1.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							 
								try {
							
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							while(scanner.hasNext()) {
								    FileWriter file = new FileWriter("C:\\xampp\\htdocs\\Arduino\\public_html\\dht.csv",true);
									String line = scanner.next();
									System.out.println(line);
									String spliting [] = line.split(",");
									System.out.println(spliting[1]);
									SendDataUsingMQTT mqtt = new SendDataUsingMQTT();
									SendTemp temp = new SendTemp();
									SendLumiere lum = new SendLumiere();
									mqtt.publish("field1="+spliting[0]);
									temp.publish("field1="+spliting[1]);
									lum.publish("field1="+spliting[2]);
									file.write(line+"\n");
									file.close();
							        
							}
							
							scanner.close();
								}catch(Exception e) {
									e.getStackTrace();
								}
							}
						
					};
					thread.start();
				} else {
					// disconnect from the serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
				}
			}
		});
		
		// show the window
		window.setVisible(true);
	}

}