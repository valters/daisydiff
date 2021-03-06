/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.outerj.daisy.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Main {
  static boolean quietMode = false;

    public static void main(final String[] args) throws URISyntaxException {

        if (args.length < 2) {
            help();
        }

        boolean htmlDiff = true;
        boolean htmlOut = true;
        String outputFileName = "daisydiff.htm";
        String[] css = new String[]{};

        InputStream oldStream = null;
        InputStream newStream = null;

        try {
            for (int i = 2; i < args.length; i++) {
                final String[] split = args[i].split("=");
                if (split[0].equalsIgnoreCase("--file")) {
                    outputFileName = split[1];
                } else if (split[0].equalsIgnoreCase("--type")) {
                    if (split[1].equalsIgnoreCase("tag")) {
                        htmlDiff = false;
                    }
                } else if (split[0].equalsIgnoreCase("--css")) {
                    css = split[1].split(";");
                } else if (split[0].equalsIgnoreCase("--output")) {
                    if (split[1].equalsIgnoreCase("xml")) {
                        htmlOut = false;
                    }
                } else if (split[0].equals("--q")){
                  quietMode = true;
                } else{
                    help();
                }

            }
            final File outputFile= new File(outputFileName);
            try {
                outputFile.createNewFile(); // Fail if outputFileName is malformed. Otherwise result.setResult() below would silently supress an exception (at least with jdk1.8.0_65). Then calling postProcess.endDocument() below would fail with confusing "javax.xml.transform.TransformerException: org.xml.sax.SAXException: setResult() must be called prior to startDocument()."
            }
            catch( final IOException e ) {
                System.err.println( "Filepath " +outputFileName+ " is malformed, or some of its folders don't exist, or you don't have write access." );
                return;
            }
            if (!quietMode){
              System.out.println("Daisy Diff https://github.com/DaisyDiff/DaisyDiff");
              System.out.println("Comparing documents: " +args[0]+ " and " +args[1] );
              System.out.println( "Diff type: " +(
                htmlDiff
                ? "html"
                : "tag"
              ) );
              System.out.println("Writing "+(htmlOut?"html":"xml")+" output to " + outputFileName);
            }

            if(css.length>0){
                if (!quietMode) {
                    System.out.println("Adding external css files:");
                }
                for(final String cssLink:css){
                    System.out.println("  "+cssLink);
                }
            }
            if (!quietMode){
              System.out.println("");
              System.out.print(".");
            }
            final SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
                    .newInstance();

            final TransformerHandler result = tf.newTransformerHandler();
            // If the file path were malformed, then the following
            result.setResult(new StreamResult(outputFile));


            if (args[0].startsWith("http://")) {
                oldStream = new URI(args[0]).toURL().openStream();
            }
            else {
                oldStream = new FileInputStream(args[0]);
            }
            if (args[1].startsWith("http://")) {
                newStream = new URI(args[1]).toURL().openStream();
            }
            else {
                newStream = new FileInputStream(args[1]);
            }

            final XslFilter filter = new XslFilter();


                final ContentHandler postProcess = htmlOut? filter.xsl(result,
                        "xslfilter/tagheader.xsl"):result;
                postProcess.startDocument();
                postProcess.startElement("", "diffreport", "diffreport",
                        new AttributesImpl());
                postProcess.startElement("", "diff", "diff",
                        new AttributesImpl());
                System.out.print(".");


                InputStreamReader oldReader = null;
                BufferedReader oldBuffer = null;

                InputStreamReader newISReader = null;
                BufferedReader newBuffer = null;
                try {
                    oldReader = new InputStreamReader(oldStream);
                    oldBuffer = new BufferedReader(oldReader);

                    newISReader = new InputStreamReader(newStream);
                    newBuffer = new BufferedReader(newISReader);
                    DaisyDiff.diffTag(oldBuffer, newBuffer, postProcess);

                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    oldBuffer.close();
                    newBuffer.close();
                    oldReader.close();
                    newISReader.close();
                }


                System.out.print(".");
                postProcess.endElement("", "diff", "diff");
                postProcess.endElement("", "diffreport", "diffreport");
                postProcess.endDocument();

        } catch (final Throwable e) {
          if (quietMode){
            System.out.println(e);
          } else {
            e.printStackTrace();
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            if (e instanceof SAXException) {
                ((SAXException) e).getException().printStackTrace();
            }
            help();
          }
        } finally {
            try {
                if(oldStream != null) {
                    oldStream.close();
                }
            } catch (final IOException e) {
                //ignore this exception
            }
            try {
                if(newStream != null) {
                    newStream.close();
                }
            } catch (final IOException e) {
                //ignore this exception
            }
        }
        if (quietMode) {
            System.out.println();
        }
        else {
            System.out.println("done");
        }

    }

    private static void doCSS(final String[] css, final ContentHandler handler) throws SAXException {
        handler.startElement("", "css", "css",
                new AttributesImpl());
        for(final String cssLink : css){
            final AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "href", "href", "CDATA", cssLink);
            attr.addAttribute("", "type", "type", "CDATA", "text/css");
            attr.addAttribute("", "rel", "rel", "CDATA", "stylesheet");
            handler.startElement("", "link", "link",
                    attr);
            handler.endElement("", "link", "link");
        }

        handler.endElement("", "css", "css");

    }

    private static void help() {
        System.out.println("==========================");
        System.out.println("DAISY DIFF HELP:");
        System.out.println("java -jar daisydiff.jar [oldHTML] [newHTML]");
        System.out
                .println("--file=[filename] - Write output to the specified file.");
        System.out
                .println("--type=[html/tag] - Use the html (default) diff algorithm or the tag diff.");
        System.out.println("--css=[cssfile1;cssfile2;cssfile3] - Add external CSS files.");
        System.out.println("--output=[html/xml] - Write html (default) or xml output.");
        System.out.println("--q  - Generate less console output.");
        System.out.println("");
        System.out.println("EXAMPLES: ");
        System.out.println("(1)");
        System.out
                .println("java -jar daisydiff.jar http://web.archive.org/web/20070107145418/http://news.bbc.co.uk/ http://web.archive.org/web/20070107182640/http://news.bbc.co.uk/ --css=http://web.archive.org/web/20070107145418/http://news.bbc.co.uk/nol/shared/css/news_r5.css");
        System.out.println("(2)");
        System.out.println("java -jar daisydiff.jar http://cocoondev.org/wiki/291-cd/version/15/part/SimpleDocumentContent/data http://cocoondev.org/wiki/291-cd/version/17/part/SimpleDocumentContent/data --css=http://cocoondev.org/resources/skins/daisysite/css/daisy.css --output=xml --file=daisysite.htm");
        System.out.println("==========================");
        System.exit(0);
    }

}
