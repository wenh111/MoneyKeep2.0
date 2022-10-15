package com.org.moneykeep.Activity.OCRView;

import java.util.List;

public class OCRBean {

    private List<WordsResultDTO> words_result;
    private Integer words_result_num;
    private Integer direction;
    private Long log_id;

    public List<WordsResultDTO> getWords_result() {
        return words_result;
    }

    public void setWords_result(List<WordsResultDTO> words_result) {
        this.words_result = words_result;
    }

    public Integer getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(Integer words_result_num) {
        this.words_result_num = words_result_num;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Long getLog_id() {
        return log_id;
    }

    public void setLog_id(Long log_id) {
        this.log_id = log_id;
    }

    public static class WordsResultDTO {
        private String words;

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}
