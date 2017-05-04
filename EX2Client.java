//Michael L, CS380
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;

public class EX2Client {

    public static void main(String[] args)
    {
        try {
            //Connecting to server
            Socket sock = new Socket("codebank.xyz", 38102);

            if(sock.isConnected()) {
                System.out.println("Connected to server");
            }

            //Reading to and from server
            InputStream is = sock.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = sock.getOutputStream();

            //storing the server message
            byte[] message = new byte[100];

            System.out.print("Received Bytes: ");

            for(int i = 0; i < 100; i++)
            {
                if(i%10 == 0)
                {
                    System.out.println("\t");
                }

                int firsth = br.read(), secondh = br.read();

                firsth = firsth << 4;
                byte val = (byte)(firsth + secondh);

                System.out.printf("%02X", val);
                message[i] = val;
            }

            //getting CRC32 code from message array
            CRC32 crc = new CRC32();
            crc.update(message);
            long errorcode = crc.getValue();
            // int errorcode32 = (int) errorcode; not needed, used to get the 32 bits of long errorcode
            System.out.printf("\nGenerated CRC code: %02X\n", errorcode);

            //Bytebuffer to sperate crc32 code into 8 bytes
            ByteBuffer bcrc = ByteBuffer.allocate(8);
            bcrc.putLong(errorcode);

            //get the lower 32 bits and send them back to server
            for(int i = bcrc.array().length/2; i < bcrc.array().length; i++)
            {
               os.write(bcrc.array()[i]);
            }

            //deciding on response
            int rs = br.read();
            if(rs == 1)
            {
                System.out.println("Response is good");
            }
            else if(rs == 0)
            {
                System.out.println("Response is bad");
            }

            //closing...
            is.close();
            os.close();
            sock.close();
            if(sock.isClosed())
            {
                System.out.println("Disconnected from server");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}

