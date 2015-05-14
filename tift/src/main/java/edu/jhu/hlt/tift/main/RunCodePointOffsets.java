/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.tift.Tokenizer;

/**
 * @author max
 * 
 */
public class RunCodePointOffsets {

    private static final Logger logger = LoggerFactory.getLogger(RunCodePointOffsets.class);

    /**
     * 
     */
    public RunCodePointOffsets() {
        // TODO Auto-generated constructor stub
    }

    /**
     * This function takes 2 arguments:
     * 
     * <pre>
     * 1) path to a file of text, and
     * 2) path to a file of tokens, one per line
     * </pre>
     * 
     * This function reads the text into a string, reads the tokens into a List
     * of Strings, then outputs code point offsets for the text-token
     * combination into a file called "output.txt".
     * 
     * @param args
     *            - length 2 array with a path to the original text, and a path
     *            to token file, one per line
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            logger.info("Usage: RunCodePointOffsets [path/to/original/text] [path/to/tokens]");
            System.exit(1);
        }

        File inputTextFile = new File(args[0]);
        File inputTokenFile = new File(args[1]);

        String text = "";
        Scanner sc = new Scanner(inputTextFile);
        while (sc.hasNextLine())
            text += sc.nextLine();
        sc.close();

        List<String> tokenList = new ArrayList<>();
        sc = new Scanner(inputTokenFile);
        while (sc.hasNextLine())
            tokenList.add(sc.nextLine());
        sc.close();

        int[] codePointOffsets = Tokenizer.getOffsets(text, tokenList);
        FileWriter fw = new FileWriter(new File("output.txt"));
        for (int i = 0; i < codePointOffsets.length; i++)
            fw.write(codePointOffsets[i] + "\n");

        fw.close();
    }
}
