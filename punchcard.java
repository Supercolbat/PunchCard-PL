import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        File f = new File();
        Parser p = new Parser();
        String arg;

        List<String> flags = new ArrayList<String>();
        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (arg.equals("-v") || arg.equals("--verbose")) {
                flags.add("verbose");
            } else {
                System.out.println("Illegal option: " + arg);
                System.exit(0);
            }
        }
        ArrayList<String> fdata = f.readf(args);
        if (flags.contains("verbose")) {
            System.out.println(" [VERBOSE] File data:"+fdata);
        }
        List<String> pdata = p.parse(fdata,flags);
        for (String out : pdata) {
			System.out.println(out);
		}
    }
}

public class File
{
	public ArrayList<String> readf(String[] args) {

        ArrayList<String> lines = new ArrayList<>();
        String[] fileND = args[args.length-1].split("\\.");

        if (fileND[fileND.length-1].equals("pc")) {
            if (args.length > 0) {
                try (Scanner s = new Scanner(new FileReader(String.join(".", fileND)))) {
                    while (s.hasNext()) {
                        lines.add(s.nextLine());
                    }
                }
                catch (FileNotFoundException e) {
                    System.out.println(String.join(".", fileND) + " not found.");
                }
            } else {
                System.out.println("Usage: punchcard [--verbose || -v] filename");
                System.exit(0);
            }
        } else {
            System.out.println("Illegal file extension.\nUsage: punchcard [--verbose -v] filename");
            System.exit(0);
        }
        return lines;
	}
}

public class Parser {
    public static void verbose(String s,int l) {if (flagl.contains("verbose")) {System.out.println(" [VERBOSE:"+l+"] "+s);}}
    private static void warn(String s,int l) {System.out.println(" [WARNING:"+l+"] "+s);}
    public static List<String> flagl = new ArrayList<String>();
    public List<String> parse(ArrayList<String> lines, List<String> flags) {
        boolean getc = true;
        String run = "";
        List<String> memory = new ArrayList<String>();
        List<String> output = new ArrayList<String>();
        int pointer = 0;
        int indline = 0;
        flagl = flags;

        for (String line : lines) {
            indline++;
			if (line.charAt(0) != ';') {
                if (line.length() >= 8) {
    	            if (line.length() > 8) {
	                    line = line.replaceAll("[^.\\s]","").substring(0,8);
	                }
	                if (getc) {
    	                if (line.equals(".  . . .")) {
        	                getc = false;
            	            run = "push";
                            Parser.verbose("cmd: push",indline);
                	    } else if (line.equals(" .. . . ")) {
                    	    memory.set(pointer, "");
                            Parser.verbose("cmd: pop",indline);
                        
        	            } else if (line.equals("..      ")) {
            	            pointer--;
                            Parser.verbose("cmd: bak",indline);
                            Parser.verbose("pointer: "+pointer,indline);
    	                } else if (line.equals("      ..")) {
        	                pointer++;
                            Parser.verbose("cmd: fwd",indline);
                            Parser.verbose("pointer: "+pointer,indline);
	                    } else if (line.equals(" .      ")) {
    	                    memory.set(pointer,Character.toString((char) Integer.parseInt(memory.get(pointer))));
                            Parser.verbose("Converted to char: "+memory.get(pointer),indline);
        	            } else if (line.equals("  .     ")) {
            	            memory.set(pointer,Integer.toString(Integer.parseInt(memory.get(pointer))+48));
                            Parser.verbose("Converted to int: "+memory.get(pointer),indline);
                	    } else if (line.equals(".  ..  .")) {
							String merged = String.join("",memory);
							memory.clear();
	                        memory.add(merged);
                            Parser.verbose("Merged all to pointer",indline);
                            Parser.verbose(memory.toString(),indline);
    	                } else if (line.equals(".  .. ..")) {
							List<String> bef = new ArrayList<String>();
        	                memory.add("tmp");
							bef.addAll(memory.subList(0,pointer));
							bef.add(String.join("",memory.subList(pointer,memory.size()-1)));
							memory.clear();
							memory.addAll(bef);
                            Parser.verbose("Merged right to pointer",indline);
                            Parser.verbose(memory.toString(),indline);
                	    } else if (line.equals(".. ..  .")) {
                            List<String> aft = new ArrayList<String>();
                            memory.add("tmp");
                            aft.add(String.join("",memory.subList(0,pointer+1)));
                            aft.addAll(memory.subList(pointer+1,memory.size()-1));
							memory.clear();
							memory.addAll(aft);
                            Parser.verbose("Merged left to pointer",indline);
                            Parser.verbose(memory.toString(),indline);
	                    } else if (line.equals(".       ")) {
    	                    output.add(memory.get(pointer));
                            Parser.verbose("Pushed to memory: "+memory.get(pointer),indline);
							memory.set(pointer,"");
                        } else {
                            Parser.warn("Unknown line on line "+indline+". Skipping. It may be useful, so check if it's written correctly:\n"+line,indline);
                        }
                    } else {
                        if (run == "push") {
                            String tmp = line.replace(".","1");
                            tmp = tmp.replace(" ", "0");
                            if (memory.size() <= pointer) {
                                memory.add(Integer.toString(Integer.parseInt(tmp,2)));
                            } else {
                                memory.set(pointer,Integer.toString(Integer.parseInt(tmp,2)));
                            }
                            Parser.verbose("pushed: "+tmp+" ("+Integer.parseInt(tmp,2)+")",indline);
                            Parser.verbose(memory.toString(),indline);
                            
                            getc = true;
                            run = "";
                        }
                    }
                } else {
                    Parser.verbose("Comment found",indline);
                }
            }
        }
        return output;
    }
}
