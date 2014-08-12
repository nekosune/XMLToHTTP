package com.nekokittygames.xmltohttp;


import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Properties;


/**
 * Created by nekosune on 11/08/14.
 */
public class XmlToHttp {


    public static void main(String[] args)
    {
        try {
            System.out.println("Loading: " + args[0]);
            File xmlFile = new File(args[0]);

            File OutputFolder=new File(xmlFile.getParent());
            System.out.println("Output Folder: "+OutputFolder);
            String outputname=xmlFile.getName();
            outputname=outputname.substring(0,outputname.length()-3)+"html";
            System.out.println("Output Name: "+outputname);
            File OutputFile=new File(OutputFolder,outputname);
            BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("chat");
            System.out.println(nList.getLength());
            Node nNode = nList.item(0);
            //System.out.println(nNode.getNodeName());
            //System.out.println(nNode.getChildNodes().item(1).getNodeValue());
            bw.write("<HTML>\r\n<HEAD>\r\n<TITLE>");
            String date=nNode.getChildNodes().item(0).getAttributes().getNamedItem("time").getNodeValue();
            DateTimeFormatter parser= ISODateTimeFormat.dateTimeNoMillis();
            LocalDateTime chatDate=parser.parseDateTime(date).toLocalDateTime();

            bw.write("Chat: "+chatDate.toString("MM/dd/yyyy HH:mm:ss"));
            bw.write("</TITLE>\r\n</HEAD>\r\n<BODY>\r\n<H1>Chat Starts: "+chatDate.toString("MM/dd/yyyy HH:mm:ss")+"</H1>\r\n");
            bw.write("<br />");
            int lastDate=chatDate.getDayOfMonth();
            boolean colour=false;
            for(int i=0;i<nNode.getChildNodes().getLength();i++)
            {
                if(!nNode.getChildNodes().item(i).getNodeName().equals("message"))
                    continue;
                bw.write("<p>\n" +
                        "        <span style=\"color: ");
                if(colour)
                    bw.write("blue");
                else
                    bw.write("red");
                colour=!colour;
                bw.write("\">");
                Node message=nNode.getChildNodes().item(i);
                bw.write(message.getAttributes().getNamedItem("sender").getNodeValue());

                String messageDate=message.getAttributes().getNamedItem("time").getNodeValue();
                LocalDateTime LocalMessageDate=parser.parseDateTime(messageDate).toLocalDateTime();
                if(LocalMessageDate.getDayOfMonth()==lastDate)
                    bw.write(" ["+LocalMessageDate.toString("HH:mm:ss")+"]</span>:\r\n");
                else
                    bw.write(" ["+LocalMessageDate.toString("MM/dd/yyyy HH:mm:ss")+"]</span>:\r\n");
                lastDate=LocalMessageDate.getDayOfMonth();
                Node div=message.getFirstChild();
                Node Span=div.getFirstChild();

                String temp=serializeDoc(Span);
                temp=temp.substring(38);
                bw.write(temp);
                bw.write("</p>");

            }



            bw.write("</BODY>\r\n</HTML>");
            bw.flush();
            bw.close();




        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String serializeDoc(Node doc) {
        StringWriter outText = new StringWriter();
        StreamResult sr = new StreamResult(outText);
        Properties oprops = new Properties();
        oprops.put(OutputKeys.METHOD, "xml");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
            t.setOutputProperties(oprops);
            t.transform(new DOMSource(doc), sr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return outText.toString();
    }
}
