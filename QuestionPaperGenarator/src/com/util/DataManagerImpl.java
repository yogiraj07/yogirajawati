package com.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bean.Category;
import com.bean.Complexity;
import com.bean.Criteria;
import com.bean.Question;
import java.sql.*;

public class DataManagerImpl implements DataManager {


	@Override
	public List<Question> populateData() {
		List<Question> questionList = new ArrayList<Question>();
		DatabaseConnectionManager dbManager = new DatabaseConnectionManager();
		Connection cn = null;
		try {
			cn = dbManager.getConnection();
			Statement stm = cn.createStatement();
			ResultSet rs = stm.executeQuery("select * from questionBank");
			while (rs.next()) {
				Question que = new Question(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),Category.valueOf(rs.getString(8)),Complexity.valueOf(rs.getString(9)));
				questionList.add(que);
				que = null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cn != null) {
				try {
					dbManager.closeConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return questionList;

	}

	@Override
	public List<Question> getQuestionByCategory(Category category,
			List<Question> questionsList) {
		List<Question> qlist = new ArrayList<Question>();
		for (Question question : questionsList) {
			if (category == question.getCategory())
				qlist.add(question);
		}
		return qlist;

	}

	@Override
	public List<Question> getQuestionByComplexity(Complexity complexity,
			List<Question> questionsList) {
		List<Question> qlist = new ArrayList<Question>();
		for (Question question : questionsList) {
			if (complexity == question.getComplexity())
				qlist.add(question);
		}
		return qlist;
	}

	@Override
	public void sortByCategory(List<Question> queList){
		//Declaring Method Local inner class for Sorting data by Category
		class SortByCategory implements Comparator<Question>{

			@Override
			public int compare(Question q1, Question q2) {
				return q1.getCategory().compareTo(q2.getCategory());
			}
			
		}		
		Collections.sort(queList,new SortByCategory());		
	}
		

	@Override
	public void sortByComplexity(List<Question> questionList) {
		//Declaring anonymous  inner class for sorting data
		// you can create completely different class as well
		
		Collections.sort(questionList,new Comparator<Question>(){

			@Override
			public int compare(Question q1, Question q2) {
				return q1.getComplexity().compareTo(q2.getComplexity());
			}			
		});
		
	}
	
	

	@Override
	public Set<Question> generateQuestionPaper(List<Question> questionsList,
			List<Criteria> template) {
		Set<Question> finalQuestionsList=new HashSet<Question>();
		/*
		 * Creating final question List using template criteria 
		 */
		for(Criteria criteria:template){
			// Searching questions of Specific Category use inbuilt method getQuestionsByCategory
			List<Question> queList= getQuestionByComplexity(criteria.getComplexity(), getQuestionByCategory(criteria.getCategory(), questionsList));
			for(int j=0;j<criteria.getNoOfQuestion();j++)
			{
				int index=(int) (Math.random()*queList.size());
				System.out.println(index);
				// Check Final Question list for eliminating duplicate questions equals() and  hashCode is implemented to achieve this
				
				int size=finalQuestionsList.size();
				
				finalQuestionsList.add(queList.get(index));
				
				if(size==finalQuestionsList.size())
					j--;
				
			}				
		}
		return finalQuestionsList;
	}

	


}