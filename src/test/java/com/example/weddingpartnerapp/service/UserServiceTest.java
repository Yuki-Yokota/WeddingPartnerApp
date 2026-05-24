package com.example.weddingpartnerapp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.CSVUpload;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.PBKDF2Util;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.CSVUser;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.repository.UserMapper;

@SpringBootTest
public class UserServiceTest {
	@Mock
	private UserMapper mock;

	@InjectMocks
	private UserServiceImpl userService;

	/**
	 * 正常系。取得したユーザデータのsaltが定義されている場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successAuthenticate() throws Exception {
		byte[] bytes = new byte[2];
		Optional<User> opt = Optional.ofNullable(new User(1, "a", "test@example.com", "c", "d", bytes));
		doReturn(opt).when(mock).findUserByMailAddress(anyString());

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.verifyPassword(anyString(), anyString(), any())).thenReturn(true);

			User user = opt.get();
			user = userService.authenticate(user);

			assertNotNull(user);
			mockedClass.verify(() -> PBKDF2Util.verifyPassword(anyString(), anyString(), any()));
		}
		verify(mock, times(1)).findUserByMailAddress(anyString());
	}

	/**
	 * 正常系。取得したユーザデータのsaltが定義されていない場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successNotSaltDataAuthenticate() throws Exception {
		Optional<User> opt = Optional.ofNullable(new User(1, "a", "b", "c", "d", null));
		doReturn(opt).when(mock).findUserByMailAddress(anyString());

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doNothing().when(mock).update(any());
			mockedClass.when(() -> PBKDF2Util.verifyPassword(anyString(), anyString(), any())).thenReturn(false);

			User user = opt.get();
			user = userService.authenticate(user);

			assertNull(user);
			mockedClass.verify(() -> PBKDF2Util.hashPassword(anyString(), any()));
			mockedClass.verify(() -> PBKDF2Util.verifyPassword(anyString(), anyString(), any()));
		}
		verify(mock, times(1)).findUserByMailAddress(anyString());
		verify(mock, times(1)).update(any());
	}

	/**
	 * 正常系。ユーザデータが見つからない場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successNotFindUserAuthenticate() throws Exception {
		User user = userService.authenticate(new User());

		assertNull(user);
	}

	/**
	 * 異常系。ハッシュ生成する際のエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_hashGenerateErrorAuthenticate() throws Exception {
		Optional<User> opt = Optional.ofNullable(new User(1, "a", "b", "c", "d", null));

		doReturn(opt).when(mock).findUserByMailAddress(anyString());

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any()))
					.thenThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR));

			User user = opt.get();
			assertThatThrownBy(() -> userService.authenticate(user)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

		verify(mock, times(1)).findUserByMailAddress(anyString());
	}
	
	/**
	 * 異常系。ハッシュパスワードと入力値を突き合わせる際のエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_hashVerifyErrorAuthenticate() throws Exception {
		Optional<User> opt = Optional.ofNullable(new User(1, "a", "b", "c", "d", null));

		doReturn(opt).when(mock).findUserByMailAddress(anyString());

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doNothing().when(mock).update(any());
			mockedClass.when(() -> PBKDF2Util.verifyPassword(anyString(), anyString(),any()))
			.thenThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR));

			User user = opt.get();
			assertThatThrownBy(() -> userService.authenticate(user)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

		verify(mock, times(1)).findUserByMailAddress(anyString());
	}

	/**
	 * 異常系。更新処理でのエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_updateErrorAuthenticate() throws Exception {
		Optional<User> opt = Optional.ofNullable(new User(1, "a", "b", "c", "d", null));

		doReturn(opt).when(mock).findUserByMailAddress(anyString());

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).update(any());

			User user = opt.get();
			assertThatThrownBy(() -> userService.authenticate(user)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

		verify(mock, times(1)).findUserByMailAddress(anyString());
		verify(mock, times(1)).update(any());
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successRegister() throws Exception {
		when(mock.count()).thenReturn(0);
		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doNothing().when(mock).insert(any());
			User user = userService.register(new User());

			mockedClass.verify(() -> PBKDF2Util.hashPassword(any(), any()));
			assertNotNull(user);
		}
		verify(mock, times(1)).count();
		verify(mock, times(1)).insert(any());
	}

	/**
	 * 正常系。ユーザがテーブルになく新規追加の場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successRegisterNotExistTable() throws Exception {
		when(mock.count()).thenReturn(null);
		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doNothing().when(mock).insert(any());
			User user = userService.register(new User());

			mockedClass.verify(() -> PBKDF2Util.hashPassword(any(), any()));
			assertNotNull(user);
		}
		verify(mock, times(1)).count();
		verify(mock, times(1)).insert(any());
	}

	/**
	 * 異常系。ハッシュ生成の際のエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_hashGenerateErrorRegister() throws Exception {
		when(mock.count()).thenReturn(0);

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(any(), any()))
					.thenThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR));
			assertThatThrownBy(() -> userService.register(new User())).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

		verify(mock, times(1)).count();
	}

	/**
	 * 異常系。挿入処理エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_insertErrorRegister() throws Exception {
		when(mock.count()).thenReturn(0);

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(anyString(), any())).thenReturn("hello");
			doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).insert(any());
			assertThatThrownBy(() -> userService.register(new User())).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}
		verify(mock, times(1)).count();
		verify(mock, times(1)).insert(any());
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successRegisterMultiple() throws Exception {
		List<User> mockList = new ArrayList<>();
		mockList.add(new User(1, "a", "b", "c", "d", null));

		doNothing().when(mock).insert(any());

		userService.registerMultiple(mockList);

		verify(mock, times(1)).insert(any());
	}

	/**
	 * 正常系。userIdがnullの場合(テーブルにデータがない場合)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successRegisterMultipleNotExistTable() throws Exception {
		List<User> mockList = new ArrayList<>();
		mockList.add(new User(null, "a", "b", "c", "d", null));

		doNothing().when(mock).insert(any());

		userService.registerMultiple(mockList);

		verify(mock, times(1)).insert(any());
	}

	/**
	 * 異常系。挿入処理エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_insertErrorRegisterMultiple() throws Exception {
		List<User> mockList = new ArrayList<>();
		mockList.add(new User(null, "a", "b", "c", "d", null));

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).insert(any());
		assertThatThrownBy(() -> userService.registerMultiple(mockList)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).insert(any());
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successGetUserList() throws Exception {
		List<User> mockList = new ArrayList<>();

		mockList = userService.findAll();

		assertNotNull(mockList);
	}

	/**
	 * 異常系。取得エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorGetUserList() throws Exception {

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();
		assertThatThrownBy(() -> userService.findAll()).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successEdit() throws Exception {
		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(any(), any())).thenReturn("hello");

			doNothing().when(mock).update(any());
			doReturn(new User()).when(mock).findById(anyInt());

			User user = userService.update("1", new User());

			assertNotNull(user);
			mockedClass.verify(() -> PBKDF2Util.hashPassword(any(), any()));
		}
		verify(mock, times(1)).update(any());
		verify(mock, times(1)).findById(anyInt());
	}

	/**
	 * 異常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_hashGenerateErrorEdit() throws Exception {

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(any(), any()))
					.thenThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR));
			assertThatThrownBy(() -> userService.update("1", new User())).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

	}

	/**
	 * 異常系_更新エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_updateErrorEdit() throws Exception {

		try (MockedStatic<PBKDF2Util> mockedClass = Mockito.mockStatic(PBKDF2Util.class)) {
			mockedClass.when(() -> PBKDF2Util.hashPassword(any(), any())).thenReturn("hello");

			doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).update(any());
			assertThatThrownBy(() -> userService.update("1", new User())).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}

		verify(mock, times(1)).update(any());
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successDelete() throws Exception {
		doNothing().when(mock).delete(anyInt());
		userService.delete("1");
		verify(mock, times(1)).delete(anyInt());
	}

	/**
	 * 異常系
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorDelete() throws Exception {

		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).delete(anyInt());
		assertThatThrownBy(() -> userService.delete("1")).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).delete(anyInt());
	}

	/**
	 * 正常系。キー値は必ず空か"a,b"の形になる。パラメタとしてASC,userIdを使用
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSortUserList() throws Exception {
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.sort(any(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.sort("1", "ASC,userId");
			mockedClass.verify(() -> Util.sort(any(), any()));
			assertNotNull(pages);
		}
	}

	/**
	 * 正常系。逆順の場合。パラメタとしてDESC,userNameを使用
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successDescendingSortUserList() throws Exception {

		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.sort(any(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.sort("1", "DESC,userName");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.sort(any(), any()));
		}
	}

	/**
	 * 正常系。パラメタとしてASC,mailAddressを使用
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSortMailSortUserList() throws Exception {
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.sort(any(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.sort("1", "ASC,mailAddress");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.sort(any(), any()));
		}
	}

	/**
	 * 正常系。パラメタとしてASC,roleを使用
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSortRoleSortUserList() throws Exception {
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.sort(any(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.sort("1", "ASC,role");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.sort(any(), any()));
		}
	}

	/**
	 * 異常系。キー値が入力されていない場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorNotEntrySortKeySortUserList() throws Exception {

		when(mock.findAllUser()).thenReturn(mock(List.class));
		assertThatThrownBy(() -> userService.sort("1", null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("入力データが不正です").hasNoCause();
	}

	/**
	 * 異常系。想定していない属性値の場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorSortUserList() throws Exception {
		when(mock.findAllUser()).thenReturn(mock(List.class));
		assertThatThrownBy(() -> userService.sort("1", "a,b")).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("入力データが不正です").hasNoCause();
	}

	/**
	 * 異常系。ソート時ユーザリスト取得でエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorfindAllUserOfSort() throws Exception {
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();
		assertThatThrownBy(() -> userService.sort("1", null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).findAllUser();

	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successFilterUserList() throws Exception {

		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.filter(any(), anyString(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.filter("1", "a,role");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.filter(any(), anyString(), any()));
		}
	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successFilterUserListOfNotExistTable() throws Exception {

		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = new ArrayList<>();
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.filter(any(), anyString(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.filter("1", "a,role");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.filter(any(), anyString(), any()));
		}
	}

	/**
	 * 異常系。キー値が入力されていない場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorNotEntraFilterKeyFilterUserList() throws Exception {
		when(mock.findAllUser()).thenReturn(mock(List.class));
		assertThatThrownBy(() -> userService.filter("1", null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("データが不正です").hasNoCause();

	}

	/**
	 * 異常系。想定していない属性値の場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorFilterUserList() throws Exception {
		when(mock.findAllUser()).thenReturn(mock(List.class));

		assertThatThrownBy(() -> userService.filter("1", "a,b")).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("入力データが不正です").hasNoCause();

	}

	/**
	 * 異常系。フィルター時ユーザリスト取得でエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorfindAllUserOfFilter() throws Exception {
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();

		assertThatThrownBy(() -> userService.filter("1", "a,b")).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).findAllUser();

	}

	/**
	 * 正常系
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSearchUserList() throws Exception {

		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.search(any(), anyString(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.search("1", "a,userName");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.search(any(), anyString(), any()));
		}
	}

	/**
	 * 正常系 リストが空の場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSearchUserListNotExistTable() throws Exception {

		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = new ArrayList<>();
		when(mock.findAllUser()).thenReturn(mockList);
		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.search(any(), anyString(), any())).thenReturn(mockList);

			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			pages = userService.search("1", "a,userName");
			assertNotNull(pages);
			mockedClass.verify(() -> Util.search(any(), anyString(), any()));
		}
	}

	/**
	 * 異常系。キー値が入力されていない場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorNotEntrySearchValueSearchUserList() throws Exception {

		when(mock.findAllUser()).thenReturn(mock(List.class));

		assertThatThrownBy(() -> userService.search("1", null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("データが不正です").hasNoCause();

	}

	/**
	 * 異常系。想定していない属性値の場合
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorSearchUserList() throws Exception {

		when(mock.findAllUser()).thenReturn(mock(List.class));

		assertThatThrownBy(() -> userService.search("1", "a,b")).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("入力データが不正です").hasNoCause();

	}

	/**
	 * 異常系。サーチ時ユーザリスト取得でエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorfindAllUserOfSearch() throws Exception {
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();

		assertThatThrownBy(() -> userService.search("1", null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();

		verify(mock, times(1)).findAllUser();

	}

	/**
	 * 正常系。登録モーダルへ遷移
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUserCaseOfRegister() throws Exception {
		Combi<Paginated<User>> combi = mock(Combi.class);
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			combi = userService.selectUser("1", "REGISTER_FORM", null);
			assertNotNull(combi);
			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}
	}

	/**
	 * 正常系。更新モーダルへ遷移
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUserCaseOfUpdate() throws Exception {
		Combi<Paginated<User>> combi = mock(Combi.class);
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			combi = userService.selectUser("1", "UPDATE_FORM", "1");
			assertNotNull(combi);
			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}
	}

	/**
	 * 正常系。削除モーダルへ遷移
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUserCaseOfDelete() throws Exception {
		Combi<Paginated<User>> combi = mock(Combi.class);
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			combi = userService.selectUser("1", "DELETE_FORM", "1");
			assertNotNull(combi);
			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}
	}

	/**
	 * 正常系。CSV登録モーダルへ遷移
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUserCaseOfCSVImport() throws Exception {
		Combi<Paginated<User>> combi = mock(Combi.class);
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			combi = userService.selectUser("1", "CSV_IMPORT_FORM", null);
			assertNotNull(combi);
			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}

	}
	
	/**
	 * 正常系。遷移しない(ページネーション用)
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successSelectUserOther() throws Exception {
		Combi<Paginated<User>> combi = mock(Combi.class);
		Paginated<User> pages = mock(Paginated.class);
		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<Util> mockedClass = Mockito.mockStatic(Util.class)) {
			mockedClass.when(() -> Util.pagenation(any(), any())).thenReturn(pages);

			combi = userService.selectUser("1", null, null);
			assertNotNull(combi);
			mockedClass.verify(() -> Util.pagenation(any(), any()));
		}

	}

	/**
	 * 異常系。更新モーダルへ遷移する際チェックされていない
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorSelectUserCaseOfUpdate() throws Exception {

		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		assertThatThrownBy(() -> userService.selectUser("1", "UPDATE_FORM", null))
				.isInstanceOf(ApplicationException.class).hasMessageContaining("1つだけ選択してください").hasNoCause();
	}

	/**
	 * 異常系。削除モーダルへ遷移する際チェックされていない
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_errorSelectUserCaseOfDelete() throws Exception {

		List<User> mockList = mock(List.class);
		when(mock.findAllUser()).thenReturn(mockList);

		assertThatThrownBy(() -> userService.selectUser("1", "DELETE_FORM", null))
				.isInstanceOf(ApplicationException.class).hasMessageContaining("1つだけ選択してください").hasNoCause();

	}

	/**
	 * 異常系。遷移時ユーザリスト取得でエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorfindAllUserOfSelectUser() throws Exception {
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();
		assertThatThrownBy(() -> userService.selectUser("1", null, null)).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).findAllUser();

	}

	/**
	 * 正常系。リストは必ず値が入っているものとする。
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_successExportCsv() throws Exception {
		User user = new User(1, "spy", "a@gmail.com", "aaa", "user", null);
		List<User> mockList = new ArrayList<>();
		mockList.add(user);
		when(mock.findAllUser()).thenReturn(mockList);
		byte[] file = new byte[2];
		try (MockedStatic<CSVDownload> mockedClass = Mockito.mockStatic(CSVDownload.class)) {
			mockedClass.when(() -> CSVDownload.getCsvFile(any(), any())).thenReturn(file);

			file = userService.exportCsv();

			assertNotNull(file);
			mockedClass.verify(() -> CSVDownload.getCsvFile(any(), any()));
		}
	}

	/**
	 * 異常系。リストが空の場合エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorListIsEmptyExportCsv() throws Exception {
		List<User> mockList = new ArrayList<>();
		when(mock.findAllUser()).thenReturn(mockList);
		assertThatThrownBy(() -> userService.exportCsv()).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("リストが空です").hasNoCause();

	}

	/**
	 * 異常系。書込み処理でエラーが発生
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_FILENOTWRITEErrorExportCsv() throws Exception {
		List<User> mockList = new ArrayList<>();
		User spyUser = Mockito.spy(new User(1, "spy", "a@gmail.com", "aaa", "user", null));
		mockList.add(spyUser);
		when(mock.findAllUser()).thenReturn(mockList);

		try (MockedStatic<CSVDownload> mockedClass = Mockito.mockStatic(CSVDownload.class)) {
			Mockito.doThrow(new ApplicationException(ErrorCode.FILE_NOT_WRITE)).when(spyUser).getUserName();
			// List<CSVUser> list = Arrays.asList(spyUser);

			// 実行
			// mockedClass.when(() -> CSVDownload.getCsvFile(list,
			// CSVUser.class)).thenReturn(file);
			assertThatThrownBy(() -> userService.exportCsv()).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("ファイルに書き込めませんでした").hasNoCause();

		}

	}

	/**
	 * 異常系。遷移時ユーザリスト取得でエラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_errorfindAllUserOfCSVExport() throws Exception {
		doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();
		assertThatThrownBy(() -> userService.exportCsv()).isInstanceOf(ApplicationException.class)
				.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		verify(mock, times(1)).findAllUser();

	}

	/**
	 * 正常系。リストは値が必ず入っているものとする ユーザIdに重複がない場合振り分けしない
	 * 
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void test_successImportCsv() throws Exception {
		List<CSVUser> mockList = new ArrayList<>();
		mockList.add(new CSVUser(2, "a", "c", "d"));
		List<User> getList = new ArrayList<>();
		getList.add(new User(1, "a", "b", "c", "d", null));
		Combi<List<User>> mockCombi = mock(Combi.class);
		MultipartFile file = mock(MultipartFile.class);

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(any(), any())).thenReturn(mockList);

			doReturn(getList).when(mock).findAllUser();
			doReturn(0).when(mock).count();

			mockCombi = userService.importCsv(file);
			assertNotNull(mockCombi);

			mockedClass.verify(() -> CSVUpload.csvUpload(any(), any()));
		}

		verify(mock, times(1)).findAllUser();
		verify(mock, times(1)).count();
	}

	/**
	 * 正常系。リストは値が必ず入っているものとする ユーザIdに重複がある場合最大値+1を振り直す
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_successDuplicateKeyImportCsv() throws Exception {
		List<CSVUser> mockList = new ArrayList<>();
		mockList.add(new CSVUser(1, "a", "c", "d"));
		List<User> getList = new ArrayList<>();
		getList.add(new User(1, "a", "b", "c", "d", null));
		Combi<List<User>> mockCombi = mock(Combi.class);
		MultipartFile file = mock(MultipartFile.class);

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(any(), any())).thenReturn(mockList);

			doReturn(getList).when(mock).findAllUser();
			doReturn(0).when(mock).count();

			mockCombi = userService.importCsv(file);
			assertNotNull(mockCombi);

			mockedClass.verify(() -> CSVUpload.csvUpload(any(), any()));
		}

		verify(mock, times(1)).findAllUser();
		verify(mock, times(1)).count();
	}

	/**
	 * 異常系。ユーザリスト取得エラー
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_findAllUserErrorItmportCsv() throws Exception {
		List<CSVUser> mockList = new ArrayList<>();
		mockList.add(new CSVUser(1, "a", "c", "d"));

		MultipartFile file = mock(MultipartFile.class);
		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(any(), any())).thenReturn(mockList);
			doThrow(new ApplicationException(ErrorCode.SYSTEM_ERROR)).when(mock).findAllUser();
			assertThatThrownBy(() -> userService.importCsv(file)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("システムエラーが発生しました").hasNoCause();
		}
		verify(mock, times(1)).findAllUser();
	}

	/**
	 * 異常系。ファイル読込時データにNumberFormatExceptionに該当するデータがある場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void errorImportCsvNumberFormat() throws Exception {
		String csvContent = "ID,名前,メールアドレス,権限\n" + "A1,依田,yoda@example.com,user";

		MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain",
				csvContent.getBytes(StandardCharsets.UTF_8));

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(file, CSVUser.class))
					.thenThrow(new ApplicationException(ErrorCode.INVALID_DATA));
			assertThatThrownBy(() -> userService.importCsv(file)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("データが不正です").hasNoCause();
			mockedClass.verify(() -> CSVUpload.csvUpload(file, CSVUser.class));
		}
	}

	/**
	 * 異常系。ファイル読込時データにNumberFormatExceptionに該当するデータがある場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void errorImportCsvCommaShortage() throws Exception {
		String csvContent = "ID,名前,メールアドレス,権限\n" + "11,依田,yoda@example.com";

		MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain",
				csvContent.getBytes(StandardCharsets.UTF_8));

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(file, CSVUser.class))
					.thenThrow(new ApplicationException(ErrorCode.INVALID_DATA));
			assertThatThrownBy(() -> userService.importCsv(file)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("データが不正です").hasNoCause();

			mockedClass.verify(() -> CSVUpload.csvUpload(file, CSVUser.class));
		}
	}

	/**
	 * 異常系。ファイル読込時データにNumberFormatExceptionに該当するデータがある場合
	 * 
	 * @throws Exception
	 */
	@Test
	public void errorImportCsvValidationError() throws Exception {
		String csvContent = "ID,名前,メールアドレス,権限\n" + "11,依田,yodaexamplecom,user";

		MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain",
				csvContent.getBytes(StandardCharsets.UTF_8));

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(file, CSVUser.class))
					.thenThrow(new ApplicationException(ErrorCode.INVALID_DATA));
			assertThatThrownBy(() -> userService.importCsv(file)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("データが不正です").hasNoCause();

			mockedClass.verify(() -> CSVUpload.csvUpload(file, CSVUser.class));
		}
	}

	/**
	 * 異常系。ファイル読込エラー(権限不足、他プロセス使用中など)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_FILENOTREADErrorImportCsv() throws Exception {
		MultipartFile file = mock(MultipartFile.class);

		try (MockedStatic<CSVUpload> mockedClass = Mockito.mockStatic(CSVUpload.class)) {
			mockedClass.when(() -> CSVUpload.csvUpload(any(), any()))
					.thenThrow(new ApplicationException(ErrorCode.FILE_NOT_READ));
			assertThatThrownBy(() -> userService.importCsv(file)).isInstanceOf(ApplicationException.class)
					.hasMessageContaining("ファイルが読み込めませんでした").hasNoCause();

			mockedClass.verify(() -> CSVUpload.csvUpload(any(), any()));
		}
	}
}
