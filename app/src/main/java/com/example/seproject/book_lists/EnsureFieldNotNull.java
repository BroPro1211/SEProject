package com.example.seproject.book_lists;

/**
 * When saving an empty list to FB, it won't save and won't appear in the data structure, and
 * hence when retrieving the data from FB to a class, the list field will be null, when it should
 * be an empty list. Hence this class keeps a private field, which can only be accessed through the
 * getField method, which ensures that field is not null.
 * @param <T> the type of the private field
 */
public abstract class EnsureFieldNotNull<T>{
    private T field;

    public EnsureFieldNotNull(){
        field = initField();
    }

    public abstract T initField();

    public T getField() {
        if (field == null)
            field = initField();
        return field;
    }
}
