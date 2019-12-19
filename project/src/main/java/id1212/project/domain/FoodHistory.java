package id1212.project.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class FoodHistory {
	private @Id @GeneratedValue Long id;
	//private Dog dog;
	private String foodName;
	private int ammount;
	
}
