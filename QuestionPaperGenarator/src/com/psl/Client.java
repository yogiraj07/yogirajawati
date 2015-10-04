package com.psl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bean.Category;
import com.bean.Complexity;
import com.bean.Criteria;
import com.bean.Question;
import com.util.DataManagerImpl;

public class Client {
	public static void main(String[] args) {
		DataManagerImpl dataManager = new DataManagerImpl();
		List<Question> queList = dataManager.populateData();

		/* Creating template for question Paper */

		List<Criteria> template = new ArrayList<Criteria>();

		template.add(new Criteria(Category.GK, Complexity.Simple, 2));
		template.add(new Criteria(Category.GK, Complexity.Medium, 1));
		template.add(new Criteria(Category.GK, Complexity.Complex, 1));
		template.add(new Criteria(Category.Science, Complexity.Complex, 1));
		template.add(new Criteria(Category.History, Complexity.Medium, 2));
		template.add(new Criteria(Category.History, Complexity.Simple, 2));
		template.add(new Criteria(Category.Geography, Complexity.Medium, 1));

		Set<Question> finalQuestionsList = dataManager.generateQuestionPaper(
				queList, template);
		System.out
				.println("****************************************************************************");
		System.out.println(" Size of Paper " + finalQuestionsList.size());
		for (Question que : finalQuestionsList) {
			System.out.println(que);
		}

	}
}
