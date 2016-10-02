package connectK;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Point;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * this class will create a new process and communicate with it using
 * the programs standard I/O. It will tell it about the game's state and then ask
 * the process for the next move.
 *
 * @author Thomas Bennett
 */
public class CPlusPlusPlayer extends GUIPlayer {

    private static final String madeMove="ReturningTheMoveMade";
    private static final int AI_MOVE=1;
    private static final int HUMAN_MOVE=-1;
    private static final int EMPTY_SPACE=0;
    private Process process;
    private static final String defaultProcessStartCommand="connectK.exe";
    private String processStartCommand;
    private boolean processStarted=false;
    private Scanner in;
    private InputStream inStream;   //this is the output from the process.
    private OutputStream out; //this is the input to the process.
	String name;

    public void cleanup (){
        process.destroy();
    }

    public CPlusPlusPlayer(byte player, BoardModel state) {
        this(player, state, defaultProcessStartCommand);
    }
    public CPlusPlusPlayer(byte player, BoardModel state, String command){
        super(player, state);
        this.processStartCommand=command;
    }


    @Override
    public Point getMove(BoardModel state, int deadline) {
        try {
            if (!processStarted)
                startProcess();
            String giveStateCommand = generateRequest(state, deadline);
            System.out.println("sending command");
            sendCommand (giveStateCommand);
            System.out.println("getting response");
            String response = getResponse();
            return parseResponse(response);
        } catch (IOException ex) {
            RuntimeException e=new RuntimeException ("problem communicating with the process: "+process+" because "+ex.getMessage(), ex);
            throw e;
        }

    }

    @Override
    public Point getMove(BoardModel state) {
        return getMove(state, -1);//deadline set to -1 if no deadline given.
    }

    private void startProcess() throws IOException {
		name = new File(processStartCommand).getName();
        System.out.println("Starting process " + name);
        ProcessBuilder pb = new ProcessBuilder(processStartCommand);
        process = pb.start();
        Runtime.getRuntime().addShutdownHook(
        		new Thread(new DestroyProcess(process))
        		);
        inStream = process.getInputStream();
        out = process.getOutputStream();
        in = new Scanner (inStream);
        processStarted = true;
    }

    private String generateRequest(BoardModel state, int deadline) {
        final String begin = "makeMoveWithState:";
        int k = state.kLength;
        String command ="";
        command+=begin+" ";
        String gravity;
        if (state.gravity)
            gravity="1";
        else
            gravity ="0";
        command+=gravity+" ";
        command +=state.width+" ";
        command +=state.height+" ";
        int ly, lx;
        if (state.lastMove==null){
            ly=-1;
            lx=-1;
        }
        else{
            ly=state.lastMove.y;
            lx=state.lastMove.x;
        }

        command +=lx+" ";
        command +=ly+" ";
        command +=deadline+" ";
        command +=k+" ";
        int huCount=0, eCount=0, aiCount=0;
        for (int col =0; col<state.width; col++){
            for (int row=0; row<state.height; row++){
                if (state.pieces[col][row]==this.player){
                    aiCount++;
                    command +=AI_MOVE+" ";
                }
                else if (state.pieces[col][row]==EMPTY_SPACE){
                    eCount++;
                    command +=EMPTY_SPACE+" ";
                }
                else if (state.pieces[col][row]==otherPlayer()){
                    huCount++;
                    command+=HUMAN_MOVE+" ";
                }
            }
        }
        System.out.println("outputting: "+aiCount+" ai pieces "+huCount+" human pieces "+eCount+" empty pieces");

        return command;
    }

    private byte otherPlayer() {
        return (byte)(this.player == 1? 2 : 1);
    }

    private void sendCommand(String command) throws IOException {
        String cmd = command.trim();
        cmd+="\n";
        out.write(cmd.getBytes());
        out.flush();


    }

    private String getResponse() {

        String response="";
        while (response.equals("")){
            response = in.nextLine();
            if (response.startsWith(madeMove)){
                return response.trim();
            }
            System.out.println("got unexpected response: "+response);
            response="";
        }
        //we'll never make it this far.
        return response;

    }

    private Point parseResponse(String response) {
        Scanner s = new Scanner(response);
        s.next(); //the start sigil.
        int col = Integer.parseInt(s.next());
        int row = Integer.parseInt(s.next());
        return new Point (col, row);
    }
    
    @Override
    public void destroy(){
    	new Thread(new DestroyProcess(process)).start();
    }
    
    class DestroyProcess implements Runnable{
    	Process process;
    	DestroyProcess(Process p){
    		process = p;
    	}
    	
		@Override
		public void run() {
			System.out.println("Destroying process " + name);
			try{
	            process.destroy();
	            Runtime rt = Runtime.getRuntime();  
	            if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) 
	               rt.exec("taskkill /f /im " + name);
	             else
	               rt.exec("killall -9 " + name);
				System.out.println("Successfully Destroyed process " + name);
	        }
	        catch (Exception ex){
	            System.err.println("Could not destroy process " + name);
	            ex.printStackTrace();
	        }	
		}
    }
}
