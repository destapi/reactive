package com.akilisha.reactive.webzy.todos;

import com.akilisha.reactive.json.JClass;
import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JReader;
import com.akilisha.reactive.json.JWriter;
import com.akilisha.reactive.webzy.todos.model.Member;
import com.akilisha.reactive.webzy.todos.model.Todo;
import com.akilisha.reactive.webzy.todos.model.TodoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class TodoListObserverTest {

    TodoListObserver obs = new TodoListObserver();
    @Mock
    PrintWriter writer;
    String writerKey = "test";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        obs.getWriters().put(writerKey, writer);
    }

    @Test
    void test_adding_new_task() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);

        // nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd0 = JClass.nodify(list);
        String str0 = JWriter.stringify(nd0);
        JNode todoListNode = JReader.parseJson(new StringReader(str0));

        assertThat(todoListNode).isNotNull();
        todoListNode.setObserver(obs);

        // add todo item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd1 = JClass.nodify(todo1);
        String str1 = JWriter.stringify(nd1);
        JNode todo1Node = JReader.parseJson(new StringReader(str1));
        assertThat(todo1Node).isNotNull();

        JNode parent = todoListNode.getItem("todos");
        parent.addItem(todo1Node);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("\"listId\": \"test\",    \"task\": \"Learn to dance\",    \"completed\": false,");
    }

    @Test
    void test_completing_a_task() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);
        // create item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo1);
        // create another item
        Todo todo2 = new Todo();
        todo2.setListId(list.getListId());
        todo2.setTask("Write a memo");
        todo2.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo2);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd0 = JClass.nodify(list);
        String str0 = JWriter.stringify(nd0);
        JNode todoListNode = JReader.parseJson(new StringReader(str0));

        assertThat(todoListNode).isNotNull();
        todoListNode.setObserver(obs);

        //complete a task
        ((JNode) ((JNode) todoListNode.getItem("todos")).getItem(0)).putItem("completed", true);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {\"completed\": \"true\"}");
    }

    @Test
    void test_removing_a_task_using_list_index() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);
        // create item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo1);
        // create another item
        Todo todo2 = new Todo();
        todo2.setListId(list.getListId());
        todo2.setTask("Write a memo");
        todo2.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo2);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd0 = JClass.nodify(list);
        String str0 = JWriter.stringify(nd0);
        JNode todoListNode = JReader.parseJson(new StringReader(str0));

        assertThat(todoListNode).isNotNull();
        todoListNode.setObserver(obs);

        //complete a task
        ((JNode) todoListNode.getItem("todos")).removeItem(0);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("\"listId\": \"test\",    \"task\": \"Learn to dance\",    \"completed\": false");
    }

    @Test
    void test_removing_a_task_using_predicate_function() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);
        // create item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo1);
        // create another item
        Todo todo2 = new Todo();
        todo2.setListId(list.getListId());
        todo2.setTask("Write a memo");
        todo2.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo2);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd0 = JClass.nodify(list);
        String str0 = JWriter.stringify(nd0);
        JNode todoListNode = JReader.parseJson(new StringReader(str0));

        assertThat(todoListNode).isNotNull();
        todoListNode.setObserver(obs);

        //complete a task
        ((JNode) todoListNode.getItem("todos")).removeWhere((t -> t.getItem("task").equals("Write a memo")));

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("\"listId\": \"test\",    \"task\": \"Write a memo\",    \"completed\": false");
    }

    @Test
    void test_sharing_task_list_with_someone() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);
        // create item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo1);
        // create another item
        Todo todo2 = new Todo();
        todo2.setListId(list.getListId());
        todo2.setTask("Write a memo");
        todo2.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo2);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd1 = JClass.nodify(list);
        String str1 = JWriter.stringify(nd1);
        JNode listNode = JReader.parseJson(new StringReader(str1));
        assertThat(listNode).isNotNull();

        assertThat(listNode).isNotNull();
        listNode.setObserver(obs);

        //create new user
        Member member = new Member();
        member.setEmail("member1@email.com");
        member.setListId(writerKey);
        member.setName("dark knight");
        member.setPhone("661-624-6276");

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd2 = JClass.nodify(member);
        String str2 = JWriter.stringify(nd2);
        JNode memberNode = JReader.parseJson(new StringReader(str2));
        assertThat(memberNode).isNotNull();

        JNode parent = listNode.getItem("sharedTo");
        parent.putItem(member.getListId(), memberNode);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"listId\": \"test\",    \"name\": \"dark knight\",    \"email\": \"member1@email.com\",    \"phone\": \"661-624-6276\"}");
    }

    @Test
    void test_remove_someone_shared_to() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);
        // create item
        Todo todo1 = new Todo();
        todo1.setListId(list.getListId());
        todo1.setTask("Learn to dance");
        todo1.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo1);
        // create another item
        Todo todo2 = new Todo();
        todo2.setListId(list.getListId());
        todo2.setTask("Write a memo");
        todo2.setStartTime(LocalDateTime.now());
        // add task
        list.getTodos().add(todo2);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd1 = JClass.nodify(list);
        String str1 = JWriter.stringify(nd1);
        JNode listNode = JReader.parseJson(new StringReader(str1));
        assertThat(listNode).isNotNull();

        //create new user
        Member member = new Member();
        member.setEmail("member1@email.com");
        member.setListId(writerKey);
        member.setName("dark knight");
        member.setPhone("661-624-6276");

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd2 = JClass.nodify(member);
        String str2 = JWriter.stringify(nd2);
        JNode memberNode = JReader.parseJson(new StringReader(str2));
        assertThat(memberNode).isNotNull();

        JNode parent = listNode.getItem("sharedTo");
        parent.putItem(member.getListId(), memberNode);

        assertThat(listNode).isNotNull();
        listNode.setObserver(obs);

        //remove sharing
        parent.removeItem(member.getListId());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"listId\": \"test\",    \"name\": \"dark knight\",    \"email\": \"member1@email.com\",    \"phone\": \"661-624-6276\"}");
    }

    @Test
    void test_updating_task_list_name() throws IOException {
        TodoList list = new TodoList();
        list.setListName("fave list");
        list.setListId(writerKey);

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode nd0 = JClass.nodify(list);
        String str0 = JWriter.stringify(nd0);
        JNode todoListNode = JReader.parseJson(new StringReader(str0));

        assertThat(todoListNode).isNotNull();
        todoListNode.setObserver(obs);

        //complete a task
        todoListNode.putItem("listName", "top 10");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {\"listName\": \"top 10\"}");
    }
}