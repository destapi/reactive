package com.akilisha.reactive.json;

import com.akilisha.reactive.json.sample.Phone;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ObserverTest {

    @Test
    void verify_adding_literal_to_array() throws IOException {
        JNode userNode = JReader.fromJson("/user-data.json");
        PrintWriter writer = mock(PrintWriter.class);
        userNode.setObserver(new AddLiteralToArrayObserver(writer));

        ((JArray) userNode.getItem("luckyNums")).addItem(1.3);
        verify(writer).println("Adding lucky number 1.3");
    }

    @Test
    void verify_updating_property_in_object() throws IOException {
        JNode userNode = JReader.fromJson("/user-data.json");
        PrintWriter writer = mock(PrintWriter.class);
        userNode.setObserver(new UpdateObjectPropertyObserver(writer));

        ((JNode) ((JNode) ((JNode) userNode.getItem("phones")).getItem(0)).getItem("number")).putItem("area", "443");
        verify(writer).println("Phone number's 'area' updated from 445 to 443");
    }

    @Test
    void verify_adding_jnode_to_array() throws IOException {
        JNode userNode = JReader.fromJson("/user-data.json");
        PrintWriter writer = mock(PrintWriter.class);
        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        userNode.setObserver(new AddJNodeToArrayObserver(writer));

        // create new list entry
        JObject office = new JObject();
        JObject officeNum = new JObject();
        officeNum.putItem("area", "567");
        officeNum.putItem("local", "343-6767");
        office.putItem("type", Phone.PhoneType.OFFICE.name());
        office.putItem("number", officeNum);

        // add new list entry
        ((JNode) userNode.getItem("phones")).addItem(office);

        Object areaCode = ((JNode) ((JNode) ((JNode) userNode.getItem("phones")).getItem(2)).getItem("number")).getItem("area");
        assertThat(areaCode.toString()).isEqualTo("567");
        verify(writer).println(message.capture());
        assertThat(message.getValue()).contains("adding office phone {type=OFFICE, number={area=567, local=343-6767}");
    }
}