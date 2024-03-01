package com.akilisha.reactive.webzy.scrum;

import com.akilisha.reactive.json.JClass;
import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JReader;
import com.akilisha.reactive.json.JWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class ScrumObserverTest {

    ScrumObserver obs = new ScrumObserver();
    @Mock
    PrintWriter writer;
    String scrumId = "1001";
    String screenName = "member1";
    Member organizer = new Member();

    private JNode mockScrum() throws IOException {
        Scrum scrum = new Scrum();
        scrum.setScrumId(scrumId);
        scrum.setChoices(List.of("1", "2", "3", "5", "8"));
        organizer.setScreenName(screenName);
        scrum.setMember(organizer);
        scrum.setTitle("Morning drive");

        // nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode scrumNode = JClass.nodify(scrum);
        String scrumStr = JWriter.stringify(scrumNode);
        JNode activeScrum = JReader.parseJson(new StringReader(scrumStr));
        assertThat(activeScrum).isNotNull();
        return activeScrum;
    }

    private JNode mockMember() throws IOException {
        Member member = new Member();
        member.setScrumId(scrumId);
        member.setScreenName("kadzo");

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode memNode = JClass.nodify(member);
        String memStr = JWriter.stringify(memNode);
        JNode newMember = JReader.parseJson(new StringReader(memStr));
        assertThat(newMember).isNotNull();
        return newMember;
    }

    private JNode mockVote() throws IOException {
        Vote vote = new Vote();
        vote.setScreenName("simba");
        vote.setScrumId(scrumId);
        vote.setChoice("3");

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode voteNode = JClass.nodify(vote);
        String voteStr = JWriter.stringify(voteNode);
        JNode newVote = JReader.parseJson(new StringReader(voteStr));
        assertThat(newVote).isNotNull();
        return newVote;
    }

    private JNode mockChoices() throws IOException {
        List<String> choices = List.of("1", "2", "3", "5", "8");

        //nodify, stringify and then parse to apply path value (in actual usage scenarios, input will almost always come in as a string)
        JNode choiceNode = JClass.nodify(choices);
        String choiceStr = JWriter.stringify(choiceNode);
        JNode newChoices = JReader.parseJson(new StringReader(choiceStr));
        assertThat(newChoices).isNotNull();
        return newChoices;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        obs.addConnection(scrumId, screenName, writer);
        organizer.setScreenName("jimmy");
        organizer.setScrumId(scrumId);
    }

    @Test
    void test_joining_scrum() throws IOException {
        JNode scrum = mockScrum();

        //add observer
        scrum.setObserver(obs);

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"scrumId\": \"1001\",    \"screenName\": \"kadzo\",    \"email\": null,    \"city\": null,    \"state\": null}");
    }

    @Test
    void test_leaving_scrum() throws IOException {
        JNode scrum = mockScrum();

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        //add observer
        scrum.setObserver(obs);

        //exit scrum
        members.removeItem(member.getItem("screenName"));

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"scrumId\": \"1001\",    \"screenName\": \"kadzo\",    \"email\": null,    \"city\": null,    \"state\": null}");
    }

    @Test
    void test_submitting_vote_in_scrum() throws IOException {
        JNode scrum = mockScrum();

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        // add observer
        scrum.setObserver(obs);

        // submit vote
        JNode vote = mockVote();
        JNode voting = scrum.getItem("voting");
        voting.putItem(vote.getItem("screenName"), vote);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"scrumId\": \"1001\",    \"screenName\": \"simba\",    \"choice\": \"3\"}");
    }

    @Test
    void test_updating_vote_in_scrum() throws IOException {
        JNode scrum = mockScrum();

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        // submit vote
        JNode vote = mockVote();
        JNode voting = scrum.getItem("voting");
        voting.putItem(vote.getItem("screenName"), vote);

        // add observer
        scrum.setObserver(obs);

        JNode prevVote = voting.getItem("simba");
        prevVote.putItem("choice", 5);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"screenName\": \"simba\",    \"from\": \"3\",    \"to\": 5}");
    }

    @Test
    void test_submitting_choices_for_scrum() throws IOException {
        JNode scrum = mockScrum();

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        // add observer
        scrum.setObserver(obs);

        // submit vote
        JNode choices = mockChoices();
        scrum.putItem("choices", choices);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: [    \"1\",    \"2\",    \"3\",    \"5\",    \"8\"]");
    }

    @Test
    void test_submitting_question_for_scrum() throws IOException {
        JNode scrum = mockScrum();

        // join scrum members
        JNode member = mockMember();
        JNode members = scrum.getItem("members");
        members.putItem(member.getItem("screenName"), member);

        // add observer
        scrum.setObserver(obs);

        // submit vote
        scrum.putItem("question", "What is the time now?");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer, new Times(2)).write(messageCaptor.capture());
        verify(writer).flush();

        assertThat(messageCaptor.getValue()).contains("data: {    \"question\": \"What is the time now?\"}");
    }
}