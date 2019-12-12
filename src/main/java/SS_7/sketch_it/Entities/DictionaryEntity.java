package SS_7.sketch_it.Entities;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Holds information for a word.
 */
@Document(collection = "dictionary")
public class DictionaryEntity {

    private String word;
    private String category;

    /**
     * Constructs a new word with a word and category.
     * @param word Word.
     * @param category Category.
     */
    public DictionaryEntity(String word, String category){
        this.category = category;
        this.word = word;
    }

    /**
     * Returns word.
     * @return Word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets word.
     * @param word Word to set.
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Returns category of word.
     * @return Category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets category of word.
     * @param category Category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Compares two words.
     * @param obj Second word to compare.
     * @return True or false.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null || this.getClass() != obj.getClass()){
            return false;
        }
        DictionaryEntity dictionaryEntity = (DictionaryEntity) obj;
        return (this.getWord().equals(dictionaryEntity.getWord()) && this.getCategory().equals(dictionaryEntity.getCategory()));
    }
}
