package com.example.weddingpartnerapp.common;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CSVDownload {
	private static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
	
	public static void addContentDisposition(HttpHeaders headers, String fileName) {
		try {
		String headerValue = String.format(CONTENT_DISPOSITION_FORMAT,
           fileName, UriUtils.encode(fileName, StandardCharsets.UTF_8.name()));
			headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);
		}catch(Exception e) {
			throw new ApplicationException(ErrorCode.FILE_NOT_WRITE);
		}
	}
	
	public static <T> String getCSVFileName(String kind) throws ApplicationException{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String datetime = sdf.format(timestamp);
		String csvFileName=kind+"_"+datetime+".csv";
		return csvFileName;
	}
	
	public static <T> byte[] getCsvFile(List<T> list,Class<T>clazz) {
        CsvMapper mapper = new CsvMapper();
        try {
	        //文字列にダブルクオートをつける
	        mapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
	        //ヘッダをつける
	        CsvSchema schema = mapper.schemaFor(clazz).withHeader();
	        if(!list.isEmpty()) {
				return mapper.writer(schema).writeValueAsString(list).getBytes("MS932");
	        }
        }catch(Exception e) {
        	throw new ApplicationException(ErrorCode.FILE_NOT_WRITE);
        }
		return null;
        
    }
}

