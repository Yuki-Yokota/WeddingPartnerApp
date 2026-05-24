package com.example.weddingpartnerapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.service.UserServiceImpl;

@SpringBootTest         
@AutoConfigureMockMvc
public class UserControllerTest {
	@MockitoBean
	private UserServiceImpl mock;

	@Autowired
	private MockMvc mockMvc;

	private String regupdJson=null;
	
	@BeforeEach
	public void setUp() {
		regupdJson = "{\"userName\" : \"依田　祐希\", "
				+ "\"mailAddress\" : \"auto@gmail.com\", "
				+ "\"password\" : \"twilight0\"}";
	}
	
	/**
	 * 正常系。userへ遷移
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successInit() throws Exception {
		SessionUser user = mock(SessionUser.class);
		List<User> list = new ArrayList<>();
		list.add(new User(1, "a", "b", "c", "d", null));
		list.add(new User(2, "a", "b", "c", "d", null));
		Paginated<User> pages = new Paginated<>();
		pages.setList(list);
		pages.setPageNum(1);
		pages.setTotalPages(1);

		doReturn(list).when(mock).findAll();
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);
			MvcResult result = mockMvc.perform(get("/user").sessionAttr("user", user)).andExpect(status().isOk())
					.andExpect(view().name("user")).andExpect(model().attribute("pageNum", 1))
					.andExpect(model().attribute("totalPages", 1)).andReturn();

			list = (List<User>) result.getModelAndView().getModel().get("userList");
			assertEquals(list.size(), 2);
			assertEquals(list.get(0).getUserId(), (Integer) 1);

			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}

		verify(mock, times(1)).findAll();
	}
	
	/**
	 * 異常系。エラーが発生する
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorInit() throws Exception {
		SessionUser user = mock(SessionUser.class);
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAll();

		mockMvc.perform(get("/user")
					.sessionAttr("user",user)
				)
				.andExpect(status().isBadRequest());
		
		verify(mock, times(1)).findAll();
	}
	
	/**
	 * 異常系。インターセプターエラーが発生する(未ログイン)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorInterceptor() throws Exception {

		mockMvc.perform(get("/user"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("login"));
		
		verify(mock, times(0)).findAll();
	}

	/**
	 * 正常系。ソート
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSort() throws Exception {
		SessionUser user = mock(SessionUser.class);
		Paginated<User> pages = Mockito.mock(Paginated.class);

		when(mock.sort("1", "ASC,userId")).thenReturn(pages);
		
		mockMvc.perform(get("/api/user")
					.param("sort", "ASC,userId")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.pageInfo").exists());
		
		verify(mock, times(1)).sort("1", "ASC,userId");
	}
	
	/**
	 * 異常系。ソート時エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorSort() throws Exception {
		SessionUser user = mock(SessionUser.class);

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR))
			.when(mock).sort(anyString(), anyString());
		
		mockMvc.perform(get("/api/user")
					.param("sort", "ASC,userId")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isBadRequest());
		
		verify(mock, times(1)).sort(anyString(), anyString());
	}
	
	/**
	 * 正常系。フィルター
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successFilter() throws Exception {
		SessionUser user = mock(SessionUser.class);
		Paginated<User> pages = mock(Paginated.class);

		when(mock.filter("1", "a,userId")).thenReturn(pages);
		
		mockMvc.perform(get("/api/user")
					.param("filter", "a,userId")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.pageInfo").exists());
		
		verify(mock, times(1)).filter("1", "a,userId");
	}
	
	/**
	 * 異常系。フィルター時エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorFilter() throws Exception {
		SessionUser user = mock(SessionUser.class);

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR))
			.when(mock).filter(anyString(), anyString());
		
		mockMvc.perform(get("/api/user")
					.param("filter", "a,userId")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isBadRequest());
		
		verify(mock, times(1)).filter(anyString(), anyString());
	}
	
	/**
	 * 正常系。サーチ
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSearch() throws Exception {
		SessionUser user = mock(SessionUser.class);
		Paginated<User> pages = mock(Paginated.class);

		when(mock.search("1", "a,userName")).thenReturn(pages);
		
		mockMvc.perform(get("/api/user")
					.param("search", "a,userName")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.pageInfo").exists());
		
		verify(mock, times(1)).search("1", "a,userName");
	}
	
	/**
	 * 異常系。サーチ時エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorSearch() throws Exception {
		SessionUser user = mock(SessionUser.class);

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR))
			.when(mock).search(anyString(), anyString());
		
		mockMvc.perform(get("/api/user")
					.param("search", "a,userName")
					.param("page", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isBadRequest());
		
		verify(mock, times(1)).search(anyString(), anyString());
	}
	
	/**
	 * 正常系。モーダル表示・ページネーション時
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUser() throws Exception {
		SessionUser user = mock(SessionUser.class);
		Paginated<User>pages = mock(Paginated.class);
		Combi<Paginated<User>> combi = new Combi<>();
		combi.setKey("a");
		combi.setData(pages);

		when(mock.selectUser("1", "REGISTER_FORM", "1")).thenReturn(combi);
		
		mockMvc.perform(get("/api/user")
					.param("page", "1")
					.param("operation", "REGISTER_FORM")
					.param("check", "1")
					.sessionAttr("user",user)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.nextForm").exists())
			.andExpect(jsonPath("$.pageInfo").exists());
		
		verify(mock, times(1)).selectUser("1", "REGISTER_FORM", "1");
	}
	
	/**
	 * 異常系。モーダル表示・ページネーション時エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorSelectUser() throws Exception {
		SessionUser user = mock(SessionUser.class);

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR))
			.when(mock).selectUser(anyString(), anyString(),anyString());
		
		mockMvc.perform(get("/api/user")
				.param("page", "1")
				.param("operation", "REGISTER_FORM")
				.param("check", "1")
				.sessionAttr("user",user)
			)
			.andExpect(status().isBadRequest());
		
		verify(mock, times(1)).selectUser(anyString(), anyString(),anyString());
	}
	
	/**
	 * 正常系。登録時
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successRegister() throws Exception {
		SessionUser loginUser = mock(SessionUser.class);
		User user = new User(null,"依田　祐希","auto@gmail.com","twilight0",null,null);

		when(mock.register(any())).thenReturn(user);
		
		mockMvc.perform(post("/api/user")
				.contentType("application/json;charset=UTF-8")
				.content(regupdJson)
				.sessionAttr("user",loginUser)
		)
		.andExpect(status().isOk());
		
		verify(mock, times(1)).register(any());
	}
	
	/**
	 * 異常系。登録時エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorRegister() throws Exception {
		SessionUser user = mock(SessionUser.class);

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR))
			.when(mock).register(any());
		
		mockMvc.perform(post("/api/user")
				.contentType("application/json;charset=UTF-8")
				.content(regupdJson)
				.sessionAttr("user",user)
		)
		.andExpect(status().isBadRequest());
			//エラーメッセージのJSONを検証
		verify(mock, times(1)).register(any());
	}
}
