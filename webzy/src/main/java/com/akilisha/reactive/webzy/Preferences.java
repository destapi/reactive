package com.akilisha.reactive.webzy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preferences implements Serializable {

    String color;
    String size;
    String brand;
}
