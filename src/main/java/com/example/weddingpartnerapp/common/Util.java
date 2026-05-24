package com.example.weddingpartnerapp.common;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.example.weddingpartnerapp.model.Paginated;

@Component
public class Util {
	
	private final static Integer PAGE_SIZE = 5;
	
	private static MessageSource messageSource;

    public Util(MessageSource messageSource) {
        Util.messageSource = messageSource;
    }

    public static String getMessage(ErrorCode errorCode) {
    	String code = String.valueOf(errorCode); 
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
	
	public static <T> Paginated<T> pagenation(List<T>list,String page){
		Paginated<T> pages = new Paginated<T>();
		int pageNum = 1;
	    if (page != null && !page.isEmpty()) {
	        pageNum = Integer.parseInt(page);
	    }
	    pages.setPageNum(pageNum);
	    int totalItems = list.size();
	    int startIndex = (pageNum - 1) * PAGE_SIZE;
	    int endIndex = Math.min(startIndex + PAGE_SIZE, totalItems);
	    list = list.subList(startIndex, endIndex);

	    int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
	    pages.setTotalPages(totalPages);
	    pages.setList(list);
		return pages;
	}

	/**
	 * ソート
	 * @param <T>
	 * @param list
	 * @param comparator
	 * @return
	 */
	public static <T> List<T> sort(List<T>list,Comparator<? super T> comparator) {
		return list.stream().sorted(comparator).collect(Collectors.toList());
	}
	/**
	 * フィルター
	 * @param <T>
	 * @param <R>
	 * @param list
	 * @param filter
	 * @param f
	 * @return
	 */
	public static <T, R> List<T> filter(List<T>list,String filter,Function<T,R> f){
		return (List<T>) list.stream().filter(item->f.apply(item).equals(filter)).collect(Collectors.toList());
	}
	/**
	 * サーチ(filterの際スペース削除、入力値ともに英字は大文字統一で検索、部分一致でok)
	 * @param <T>
	 * @param <R>
	 * @param list
	 * @param searchValue
	 * @param f
	 * @return
	 */
	public static <T,R> List<T> search(List<T>list,String searchValue,Function<T,String> f){
		return list.stream()
				.filter(wed->f.apply(wed).replaceAll(" ","").toUpperCase().contains(searchValue))
				.collect(Collectors.toList());
	}
}
