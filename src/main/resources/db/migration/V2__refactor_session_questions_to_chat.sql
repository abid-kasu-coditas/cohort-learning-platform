ALTER TABLE session_questions DROP COLUMN is_answered;
ALTER TABLE session_questions DROP COLUMN answered_at;
ALTER TABLE session_questions RENAME COLUMN question_text TO message_text;
ALTER TABLE session_questions RENAME COLUMN asked_by TO sent_by;
ALTER TABLE session_questions RENAME COLUMN asked_at TO sent_at;
