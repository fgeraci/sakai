package org.sakaiproject.rubrics.api.rubric;


public class RubricGrade {
	
	
		private Long gradeDetailId;
		private String submissionId;
		private Long rubricCellId;
		private Double pointsEarned;
		private String comment;
		
		public Long getGradeDetailId() {
			return gradeDetailId;
		}
		public void setGradeDetailId(Long id) {
			this.gradeDetailId = id;
		}
		public Long getRubricCellId() {
			return rubricCellId;
		}
		public void setRubricCellId(Long cellId) {
			this.rubricCellId = cellId;
		}
		public Double getPointsEarned() {
			return pointsEarned;
		}
		public void setPointsEarned(Double points) {
			this.pointsEarned = points;
		}
		
		public void setAssignmentSubmissionId(String pSubmissionID) {
			this.submissionId = pSubmissionID;
		}
		
		public String getAssignmentSubmissionId() {
			return this.submissionId;
		}
		
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}

	}
