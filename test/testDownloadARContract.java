
import org.rmj.gocas.DownloadARContract;

public class testDownloadARContract { 
    public static void main(String [] args){
        String [] argx = new String [3];

        argx[0] = "IntegSys";
        argx[1] = "M001111122";
        argx[2] = "M00125000084";
        
        DownloadARContract.main(argx);
    }
}
