package com.univ.haifa.asai.model;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.SubfieldImpl;

import com.google.gson.Gson;

public class Asai {
	
	//final static File file = new File("C:\\Users\\ajacobsmo\\Desktop\\asai\\04-09-2022-18-03-25-clean.json");
	//final static File file = new File("/exlnewdrv/exlibris4_V22/local_apps/user/adi/06-10-2022-11-50-55.json");
	
	final static String DIR_NAME = "/home/adi/asai";
	static File file = getJsonFile();
	
	//(windows) final static File primoAsaiDir = new File("C:\\Users\\ajacobsmo\\Desktop\\asai\\primo_asai_out");
	//final static File primoAsaiDir = new File("/exlnewdrv/exlibris4_V22/local_apps/user/adi/primo_asai_out");
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
	static LocalDateTime now = LocalDateTime.now();
	final static File primoAsaiDir = new File(DIR_NAME + "/ASAI_" + dtf.format(now));
	final static File backupDir = new File(DIR_NAME + "/old_json");
	final static int PRIMO_HEADER_ID_LEN = 12;
	
	public static void main(String[] args) {
			
		if (!primoAsaiDir.exists()){
			  primoAsaiDir.mkdirs();
		}
		if (!backupDir.exists()){
			backupDir.mkdirs();
		}
		ArrayList<Model> models = extractJsonData();
		  for (int i=0; i<models.size(); i++) {
			  ArrayList<String> asai = models.get(i).title.getSubFieldValue();
			  String asaiNum = asai.get(0).substring(6);
			  wrapXmlWithPrefixAndSuffix(createXmlFileFromJsonData(models.get(i), i), i, asaiNum);
		}
		//linux code - start: make tar.gz file, move old json input file to backup directory and delete out put directory which is now tar.gz
		try {
			createTarGzipFolder(primoAsaiDir.toPath());
			file.renameTo(new File(backupDir + "/" + file.getName())); 
			deleteDirectory(primoAsaiDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unable to create tar.gz file");
		}
		//linux code end
	}
	
	public static void deleteDirectory(File file) {
		String[]entries = file.list();
		for(String s: entries){
		    File currentFile = new File(file.getPath(),s);
		    currentFile.delete();
		}
		file.delete();
    }
	
	private static File getJsonFile() {
		File file = null;
		File folder = new File(DIR_NAME);
		File[] listOfFiles = folder.listFiles();

		for (int i=0; i<listOfFiles.length; i++){
		  if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".json")){
			  file = listOfFiles[i];
		  }
		}
		return file;
	}
	
//	public File jsonToMarcXmlProcess() {
//		  
//		  return primoAsaiDir;
//	  }
	  
	  private static ArrayList<Model> extractJsonData() {
		
		  ArrayList<Model> models = new ArrayList<Model>();
		  //Model model = null;
		  JSONArray objs;
		try {
//			File convFile = new File( file.getOriginalFilename());
//			file.transferTo(convFile);
			InputStream is = new FileInputStream(file);
			Charset charset = StandardCharsets.UTF_8;
			Reader r = new InputStreamReader(is, charset);
			objs = (JSONArray) new JSONParser().parse(r);
			for(int i=0; i<objs.size(); i++) {
				JSONObject jo = (JSONObject) objs.get(i);
				JSONObject metaData = (JSONObject) jo.get("metaData");
		        JSONObject data = (JSONObject) metaData.get("Data");
		     
		        Gson gson = new Gson();
		        Model model = gson.fromJson(data.toJSONString(), Model.class);
		        if (model.asainumber != null) {
		        	models.add(model);
		        }
			}
	        
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return models;
	  }
	  
	  private static void addAsaiConstantFields(Record marcXmlRecord) {
		  
		  ArrayList<GenericField> constantFields = new ArrayList<GenericField>();
		  constantFields.add(new Constant("040", new char[]{'b', 'e'}, new String[]{"heb", "rda"}, ' ' , ' '));
		  constantFields.add(new Constant("336", new char[]{'a', 'b', '2'}, new String[]{"text", "txt", "rdacontent"}, ' ' , ' '));
		  constantFields.add(new Constant("337", new char[]{'a', 'b', '2'}, new String[]{"computer", "c", "rdamedia"}, ' ' , ' '));
		  constantFields.add(new Constant("338", new char[]{'a', 'b', 'c'}, new String[]{"online resource", "cr", "rdacarrier"}, ' ' , ' '));
		  constantFields.add(new Constant("347", new char[]{'a', 'b', '2'}, new String[]{"image file", "TIF", "rda"}, ' ' , ' '));
		  constantFields.add(new Constant("347", new char[]{'a', 'b', '2'}, new String[]{"text file", "PDF", "rda"}, ' ' , ' '));
		  constantFields.add(new Constant("542", new char[]{'a'}, new String[]{"מצויות בידי ארכיון הסיפור העממי בישראל ע\"ש דב נוי (אסע\"י) באוניברסיטת חיפה. השימוש בסיפור מחייב לציין את שמו המלא של הארכיון:  \"ארכיון הסיפור העממי בישראל ע\"ש דב נוי (אסע\"י), באוניברסיטת חיפה\" ואת הפרטים הבאים: שם רושם/רושמת הסיפור, שם מספר/ת הסיפור, עדת המוצא, המספר הסידורי של הסיפור בארכיון"}, ' ' , ' '));
		  constantFields.add(new Constant("552", new char[]{'a', 'u'}, new String[]{"הסיפור נקלט ותועד ע\"י ארכיון הסיפור העממי בישראל ע\"ש דב נוי (אסע\"י), באוניברסיטת חיפה", "http://ifa.haifa.ac.il"}, ' ' , ' '));
		  constantFields.add(new Constant("583", new char[]{'a', 'k'}, new String[]{"התאמת הרשומה לקטלוג:", "ספריית אוניברסיטת חיפה"}, ' ' , ' '));
		  constantFields.add(new Constant("595", new char[]{'a'}, new String[]{"ASAI" + LocalDate.now().getYear()}, ' ' , ' '));
		  //מורידה לבקשת ניר constantFields.add(new Constant("710", new char[]{'a', '9', 'e'}, new String[]{"ארכיון הספור העממי בישראל ע\"ש דב נוי", "heb", "גוף מנפיק"}, '2' , ' '));
		  constantFields.add(new Constant("999", new char[]{'a'}, new String[]{"DOC"}, ' ' , ' '));
		  constantFields.add(new Constant("999", new char[]{'a'}, new String[]{"DTEXT"}, ' ' , ' '));
		  


		  for (GenericField constant : constantFields) {
			  DataFieldImpl marcXmlDataField = new DataFieldImpl(constant.TAG, constant.IND1, constant.IND2);
		      for (int i=0; i<constant.SUB_FIELD_CODE.length; i++) {
		     	 SubfieldImpl marcXmlSubField = new SubfieldImpl(constant.SUB_FIELD_CODE[i], constant.SUB_FIELD_VALUES[i]);//char code, string data
		     	 marcXmlDataField.addSubfield(marcXmlSubField);
		      }
		      marcXmlRecord.addVariableField(marcXmlDataField);
		  }
	  }
	  
	  private static File wrapXmlWithPrefixAndSuffix(File marcXml, int index, String asaiNum) {
		  
		  try {
			  //(windows)File tempFile = new File(primoAsaiDir + "\\\\asai_out_" + index + ".xml");
			  File tempFile = new File(primoAsaiDir + "/asai_out_" + index + ".xml");

			  BufferedReader reader = new BufferedReader(new FileReader(marcXml, StandardCharsets.UTF_8));
			  BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, StandardCharsets.UTF_8));
			  
			  int i = PRIMO_HEADER_ID_LEN - asaiNum.length();
			  StringBuffer id = new StringBuffer();
			  for(int j=0; j<i; j++) {
				  id.append("0");
			  }
			  id.append(asaiNum);
			  
			  String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
			      		+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\r\n"
			      		+ "	<ListRecords>\r\n"
			      		+ "		<record>\r\n"
			      		+ "			<header>\r\n"
			      		+ "				<identifier>urm-publish:" + id + "</identifier>\r\n"
			      		+ "			</header>\r\n"
			      		+ "			<metadata>"
			      		+ "	"
			      		+ "			";
			  writer.write(prefix + System.getProperty("line.separator"));
			  
			  //String lineToRemove = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>";
			  String lineToRemove = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><marc:collection xmlns:marc=\"http://www.loc.gov/MARC21/slim\">";
			  String endlineToRemove = "</marc:collection>";
			  String currentLine;

			  while((currentLine = reader.readLine()) != null) {
			      // trim newline when comparing with lineToRemove
			      String trimmedLine = currentLine.trim();
			      if(trimmedLine.contains(lineToRemove) || trimmedLine.contains(endlineToRemove)) continue;
			      currentLine = currentLine.replaceFirst("<marc:", "<");
			      currentLine = currentLine.replace("</marc:", "</");
			      writer.write("			" + currentLine + System.getProperty("line.separator"));
			  }
		      String suffix = "			</metadata>\r\n"
	    		+ "		</record>\r\n"
	    		+ "	</ListRecords>\r\n"
	    		+ "</OAI-PMH>";
		      writer.write(suffix);
			  writer.close(); 
			  reader.close(); 
			  return tempFile;
		  } catch (Exception e) {
			  throw new RuntimeException("Could not wrap the file with primo prefix and suffix! error caused by: " + e.getMessage());
		}
		  
//		  FileOutputStream fos = null;
//	      try {
//	          FileInputStream in = new FileInputStream(marcXml);
//		      File file = File.createTempFile("bib4_", null);
//		      fos = new FileOutputStream(file);
//		      String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
//		      		+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\r\n"
//		      		+ "	<ListRecords>\r\n"
//		      		+ "		<record>\r\n"
//		      		+ "			<header>\r\n"
//		      		+ "				<identifier>aleph-publish:000003834</identifier>\r\n"
//		      		+ "			</header>\r\n"
//		      		+ "			<metadata>"
//		      		+ "				";
//		      byte[] array = prefix.getBytes();
//		      fos.write(array);
//		      int n;
//		      while ((n = in.read()) != -1) {
//	              // write() function to write
//	              // the byte of data
//		    	  fos.write(n);
//	          }
//		      String suffix = "			</metadata>\r\n"
//			      		+ "		</record>\r\n"
//			      		+ "	</ListRecords>\r\n"
//			      		+ "</OAI-PMH>";
//		      byte[] suffixArr = suffix.getBytes();
//		      fos.write(suffixArr);
//		      fos.close();
//		      in.close();
//		      return file;
//		  } catch (Exception e) {
//			    throw new RuntimeException("Could not read the file!");
//		  }
		   
	  }
	  
  	// generate .tar.gz file at the current working directory
    // tar.gz a folder
     public static void createTarGzipFolder(Path source) throws IOException {

         if (!Files.isDirectory(source)) {
             throw new IOException("Please provide a directory.");
         }

         // get folder name as zip file name
         String tarFileName = source.getFileName().toString() + ".tar.gz";

         try (OutputStream fOut = Files.newOutputStream(Paths.get(tarFileName));
              BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
              GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
              TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

             Files.walkFileTree(source, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file,
                                            BasicFileAttributes attributes) {

                    // only copy files, no symbolic links
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }

                    // get filename
                    Path targetFile = source.relativize(file);

                    try {
                        TarArchiveEntry tarEntry = new TarArchiveEntry(
                                file.toFile(), targetFile.toString());

                        tOut.putArchiveEntry(tarEntry);

                        Files.copy(file, tOut);

                        tOut.closeArchiveEntry();

                        System.out.printf("file : %s%n", file);

                    } catch (IOException e) {
                        System.err.printf("Unable to tar.gz : %s%n%s%n", file, e);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.printf("Unable to tar.gz : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }

            });

            tOut.finish();
         }

     }
	  
	  private static File createXmlFileFromJsonData(Model data, int index) {
		  FileOutputStream fos = null;
		  MarcXmlWriter writer = null;
		  try {
		      //File file = File.createTempFile("C:\\\\Users\\\\ajacobsmo\\\\Desktop\\\\asai\\\\asai_out\\\\bib3_" + index, null);
		      File file = File.createTempFile("/home/adi/asai/temp/asai_out/bib3_"+ index, null);
		      fos = new FileOutputStream(file);
//		      fd = fos.getFD();
//		      String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
//		      		+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\r\n"
//		      		+ "	<ListRecords>\r\n"
//		      		+ "		<record>\r\n"
//		      		+ "			<header>\r\n"
//		      		+ "				<identifier>aleph-publish:000003834</identifier>\r\n"
//		      		+ "			</header>\r\n"
//		      		+ "			<metadata>"
//		      		+ "				";
//		      byte[] array = prefix.getBytes();
//		      fos.write(array);
		      writer = new MarcXmlWriter(fos, true);
		      MarcFactory factory = MarcFactory.newInstance();
		      Record marcXmlRecord = factory.newRecord("00000nam a2200000 i 4500");
//		      Record marcXmlRecord = new RecordImpl();
		      Date date = new Date();
		      SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		      //ControlFieldImpl controllField = new ControlFieldImpl("008", "s1971 is s ||| | heb d");
		      ControlFieldImpl controllField = new ControlFieldImpl("008", formatter.format(date));
//		      LeaderImpl leader = new LeaderImpl("00000nam a2200000 i 4500");
//		      marcXmlRecord.setLeader(leader);
		      marcXmlRecord.addVariableField(controllField);
		      addAsaiConstantFields(marcXmlRecord);
		      Field[] dataFields = data.getClass().getFields();
		      for(int i = 0; i < dataFields.length; i++) {
		         //System.out.println("The field is: " + dataFields[i].getName());
		         Field field = Model.class.getField(dataFields[i].getName());
		         GenericField f;
		         try {
			         f = (GenericField)field.get(data);
		         }
		         catch (Exception e) {
					continue;
				}
		         //No such feild in model
		         if (f == null) {
		        	 continue;
		         }
		         ArrayList<String> subFieldDataTextValues = ((GenericField)(field.get(data))).getSubFieldValue();
		         String[] subFieldValuesOriginal = f.SUB_FIELD_VALUES;
		         for (String subFieldDataTextValue : subFieldDataTextValues) {
			         String[] subFieldValues = subFieldValuesOriginal.clone();
			         if (subFieldDataTextValue.contains("|")) {
			        	 int counter = 0;
			        	 for(String x : subFieldValues){
		        		    if(x!=null && x.equals("|")){
		        		    	counter++;
		        		    }
		        		 }
			        	 if (counter >= 2) {
			        		 counter = 1;
			        		 String[] values = subFieldDataTextValue.split(" \\| ");
			        		 for (int k=0; k<subFieldValues.length; k++) {
			        			 if (subFieldValues[k].equals("|")) {
				        			 subFieldValues[k] = counter == 1 ? subFieldValues[k].replace("|", values[1]) : subFieldValues[k].replace("|", values[0]);
				        			 counter++;
			        			 }
			        		 }
		
			        	 }
			         }
			         DataFieldImpl marcXmlDataField = new DataFieldImpl(f.TAG, f.IND1, f.IND2);
			         for (int j=0; j<subFieldValues.length; j++) {
			        	 String value = subFieldValues[j] != null ? subFieldValues[j] : f.SUB_FIELD_CODE[j] != '9' ? subFieldDataTextValue : ((GenericField)(field.get(data))).getSubFieldLangValue();
			        	 
			        	 SubfieldImpl marcXmlSubField = new SubfieldImpl(f.SUB_FIELD_CODE[j], value);//char code, string data
			        	 marcXmlDataField.addSubfield(marcXmlSubField);
			         }
			         marcXmlRecord.addVariableField(marcXmlDataField);
		         }

		      }
		      
		      //Fixes manually!
		      //Title and asia number are unique fields - should edit it manually and should add 246 field
		      DataFieldImpl title = (DataFieldImpl)marcXmlRecord.getVariableField("245");
		      DataFieldImpl asaiNumber = (DataFieldImpl)marcXmlRecord.getVariableField("093");
		      String titleSubFieldDataA = title.getSubfield('a').getData();
		      title.getSubfield('a').setData(titleSubFieldDataA + " - " + asaiNumber.getSubfield('a').getData());
		      DataFieldImpl marcXmlDataField = new DataFieldImpl("246", '3', '3');
		      SubfieldImpl marcXmlSubField = new SubfieldImpl('a', asaiNumber.getSubfield('a').getData() + " - " + titleSubFieldDataA);//char code, string data
		      marcXmlDataField.addSubfield(marcXmlSubField);
		      marcXmlRecord.addVariableField(marcXmlDataField);
		      asaiNumber.getSubfield('a').setData(titleSubFieldDataA);
		      //Edit controlfield 008
		      String yearInControlField = "n####";
		      VariableField year = marcXmlRecord.getVariableField("264");
		      if (year != null) {
		    	  yearInControlField = "s" + ((DataFieldImpl) year).getSubfield('c').getData();
		      }
		      else {
		    	  DataFieldImpl yearMarcXmlDataField = new DataFieldImpl("264", ' ', '1');
			      SubfieldImpl yearMarcXmlSubField = new SubfieldImpl('c', "0");//char code, string data
			      yearMarcXmlDataField.addSubfield(yearMarcXmlSubField);
			      marcXmlRecord.addVariableField(yearMarcXmlDataField);
		      }
		      List<ControlField> cfl = marcXmlRecord.getControlFields();
		      for (ControlField cf : cfl) {
		    	  if(cf.getTag().equals("008")) {
		    		  String cfData = cf.getData();
		    		  String updatedCf = cfData + yearInControlField + "is######s#####0|0#|#heb#d";
		    		  cf.setData(updatedCf);
		    	  }
		      }
		      
		      List<VariableField> allRelationDFInRecord = marcXmlRecord.getVariableFields("690");
		      Collections.sort(allRelationDFInRecord, new Comparator<VariableField>() {
		    	  @Override
		    	  public int compare(VariableField vf1, VariableField vf2) {
		    		DataField df1 = (DataField)vf1;
		    		DataField df2 = (DataField)vf2;
		    	    return ((Character.toString(df1.getSubfields().get(0).getCode())).compareTo(Character.toString(df2.getSubfields().get(0).getCode())));
		    	  }
		      });
		      for (VariableField vf : allRelationDFInRecord) {
		    	  marcXmlRecord.removeVariableField(vf);
		      }
		      for (VariableField vf : allRelationDFInRecord) {
		    	  marcXmlRecord.addVariableField(vf);
		      }
		      
		      List<DataField> allDFInRecord = marcXmlRecord.getDataFields();
		      //Collections.sort(allDFInRecord, (DataField df1, DataField df2) -> df1.tag-df2.tag);
		      //allDFInRecord.sort((df1,df2->df1.getTimeStarted().compareTo(df2.getTimeStarted())));
		      Collections.sort(allDFInRecord, new Comparator<DataField>() {
		    	  @Override
		    	  public int compare(DataField df1, DataField df2) {
		    	    return df1.getTag().compareTo(df2.getTag());
		    	  }
		      });
		      
		      writer.write(marcXmlRecord);
		      // flush data from the stream into the buffer
//		      fos.flush();
		      // confirms data to be written to the disk
//		      fd.sync();
//		      System.out.println(marcXmlRecord.toString());
		      // create in
//		      String suffix = "			</metadata>\r\n"
//			      		+ "		</record>\r\n"
//			      		+ "	</ListRecords>\r\n"
//			      		+ "</OAI-PMH>";
//		      byte[] suffixArr = suffix.getBytes();
//		      fos.write(suffixArr);
//		      writer.close();
		      return file;
		  } catch (Exception e) {
		    throw new RuntimeException("Eception occured with message: " + e.getMessage());
		  } finally {
			  if(fos!=null && writer!=null) {
//				  String suffix = "			</metadata>\r\n"
//				      		+ "		</record>\r\n"
//				      		+ "	</ListRecords>\r\n"
//				      		+ "</OAI-PMH>";
//			      byte[] suffixArr = suffix.getBytes();
//			      try {
//					fos.write(suffixArr);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			      writer.close();
			  }  
		}
	  }

}
