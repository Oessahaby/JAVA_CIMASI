package jSensors;
import java.util.List;
import com.profesorfalken.jsensors.JSensors;                      
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;    
import com.profesorfalken.jsensors.model.sensors.Fan;                  
import com.profesorfalken.jsensors.model.sensors.Temperature;
public class Main  {
	public static int value = 0;
    public static void main(String[] args) {
    }
    public void run() {
        Components components = JSensors.get.components();
        List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                  System.out.println("Sensors: ");
                  List<Temperature> temps = cpu.sensors.temperatures;
                  for (final Temperature temp : temps) {
                	  Main.value = (int)Math.round(temp.value);
                      System.out.println(temp.name + ": " + temp.value + " C");
                  }
                  List<Fan> fans = cpu.sensors.fans;
                  for (final Fan fan : fans) {
                      System.out.println(fan.name + ": " + fan.value + " RPM");
                  }
                }
            }
        }
        
    }
}