package edu.jhu.hlt.tift.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.tift.Tokenizer;

/*
 * @author tanx
 * 
 */
public class AnnoPipelineCommand {

	private static final Logger logger = LoggerFactory.getLogger(AnnoPipelineCommand.class);
	//private static final String TokenizerTypePrefix = "edu.jhu.hlt.tift.Tokenizer.";
	
	/**
     * 
     */
    public AnnoPipelineCommand() {
        // TODO Auto-generated constructor stub
    }

	/*
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		if (args.length != 2) {
			logger.info("Usage: AnnoPipelineCommand [tokenization_type] [path/to/text/file]");
		}

		String tokenizationType = args[0];
		Tokenizer tokenizer = null;
				
		for(Tokenizer curTokenizer : Tokenizer.class.getEnumConstants()){
			if(curTokenizer.name().equalsIgnoreCase(tokenizationType))
				tokenizer = curTokenizer;
		}

		if(tokenizer == null){
			logger.info("Invalid Tokenization Type: "+args[0]
					+"\n Must be <PTB>,<WHITESPACE>,<TWITTER_PETROVIC>,<TWITTER>, or <BASIC>"
					+"\n Default go with: BASIC");
			tokenizer = Tokenizer.BASIC;
		}
		
		File input = new File(args[1]);
        Scanner sc = new Scanner(input);
        
        while (sc.hasNextLine()){
        	String line = sc.nextLine();
        	if(line.matches("^<.*>$")) 
        		System.out.println(line);
        	else{
        		String text = "";
        		List<String> tokenList = tokenizer.tokenize(line);
        		for(String tok : tokenList)
        			text += tok + " ";
        		System.out.println(text);
        	}
        }
        sc.close();
	}
}
