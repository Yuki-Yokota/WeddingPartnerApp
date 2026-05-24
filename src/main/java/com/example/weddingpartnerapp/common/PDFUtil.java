package com.example.weddingpartnerapp.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Component
public class PDFUtil {
	private final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
	
	public void addContentDisposition(HttpHeaders headers, String fileName) {
		try {
		String headerValue = String.format(CONTENT_DISPOSITION_FORMAT,
           fileName, UriUtils.encode(fileName, StandardCharsets.UTF_8.name()));
			headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);
		}catch(Exception e) {
			throw new ApplicationException(ErrorCode.FILE_NOT_WRITE);
		}
	}
    /**
     * PDF出力メイン処理
     * @param data
     * @throws ApplicationException
     */
    public byte[] outputPDF(String subject,String[][] data) throws ApplicationException{
	    // ドキュメントオブジェクトの作成
	    PDDocument document = new PDDocument();
	    try {
	        // ページオブジェクトの作成(A4サイズ)
	        PDRectangle pageSize = PDRectangle.A4;
	        PDPage page = new PDPage(pageSize);
	        document.addPage(page);

	        //文字出力処理
	        PDPageContentStream contentStream = new PDPageContentStream(document, page);
	        
	        drawTable(document,page, contentStream, subject,data, 700, 100);
	        
	        contentStream.close();
	    }
	    catch (IOException e) {
	        throw new ApplicationException(ErrorCode.NOT_CONVERT_TO_PDF);
	    }
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] file=null;
	        
	    try {   
	        document.save(baos);
	        file = baos.toByteArray();
	        document.close();
	    }catch(IOException e) {
	    	throw new ApplicationException(ErrorCode.NOT_SAVE_OPEN_TO_PDF);
	    }
	    
	    return file;
	}
    
	
	public String getTimeStamp(String funcName) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String datetime = sdf.format(timestamp);
		return funcName + "_" + datetime +".pdf";
	}
	
	/**
	 * PDFにテーブルを描画する
	 * @param document
	 * @param page
	 * @param content
	 * @param data
	 * @param posY
	 * @param margin
	 * @throws ApplicationException
	 * @throws IOException
	 */
	public void drawTable(PDDocument document,PDPage page, PDPageContentStream content, String subject,String[][] data, float posY, float margin)throws ApplicationException, IOException {
		//フォント指定
        File file = new File("C:/Windows/Fonts/msmincho.ttc");
        PDFont font = null;
        try (TrueTypeCollection collection = new TrueTypeCollection(file)) {
			font = PDType0Font.load(document, collection.getFontByName("MS-Mincho"), true);
			content.setFont(font, 12);
		}
	    float fontSize = 12;
	    float fontHeight = font.getFontDescriptor()
	            .getFontBoundingBox().getHeight() / 1000 * fontSize;
	    // ページの高さ・長さを取得
	    float pageHeight = page.getMediaBox().getHeight();
	    float pageWidth = page.getMediaBox().getWidth();
	    
	    final int rows = data.length;
	    final int cols = data[0].length;
	    final float rowHeight = fontHeight * 1.5f;
	    final float tableWidth = page.getMediaBox().getWidth() - margin * 2;
	    final float tableHeight = rowHeight * rows;
	    final float colWidth = tableWidth / cols;

	    content.beginText();
	    content.newLineAtOffset(pageWidth / 2 - 20, pageHeight - fontHeight - 50);
	    content.showText(subject);
	    content.endText();
        
	    float y = posY;
	    for (int i = 0; i <= rows; i++) {
	        content.moveTo(margin, y);
	        content.lineTo(margin + tableWidth, y);
	        content.stroke();
	        y -= rowHeight;
	    }

	    float x = margin;
	    for (int i = 0; i <= cols; i++) {
	        content.moveTo(x, posY);
	        content.lineTo(x, posY - tableHeight);
	        content.stroke();
	        x += colWidth;
	    }

	    final float cellMargin = 5;
	    float textX = margin + cellMargin;
	    float textY = posY - fontHeight;
	    for (final String[] rowData : data) {
	        for (String text : rowData) {
	            content.beginText();
	            content.newLineAtOffset(textX, textY);
	            content.showText(text);
	            content.endText();
	            textX += colWidth;
	        }
	        textY -= rowHeight;
	        textX = margin + cellMargin;
	    }
	    
	    content.beginText();
	    content.newLineAtOffset(margin, tableHeight - fontHeight);
	    content.showText("※ケーキ代35,000、サービス料:合計*10%、消費税10%を含みます");
	    content.endText();
	}
}
