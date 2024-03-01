package com.akilisha.reactive.json;

/**
 * Observer contains method for accepting events around changes in data for to Object of type JNode. Objects of this type
 * (JArray and JObject) are 'reactive aware', meaning that they are able to know when any attribute inside themselves is
 * changed, either through addition, updating or removal. This capability can be harnessed to using a concrete representation
 * of the Observer, and should be writen to any concrete Writer of interest
 */
public interface Observer {

    /**
     * applies to jarray
     *
     * @param target represents the immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value  the value that was added to the array
     */
    default void add(Object target, String path, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target   represents immediate parent of the JSON attribute whose data is being changed
     * @param path     represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key      key used to identify a value in a dictionary
     * @param oldValue the value identified by the key 'key' in the dictionary before the update
     * @param newValue the new value that will be identified by the key 'key' in the dictionary
     */
    default void set(Object target, String path, String key, Object oldValue, Object newValue) {
    }

    /**
     * applies to jarray - retrieve from an array using a predicate function
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value  value in an array that has been accessed
     */
    default void get(Object target, String path, Object value) {
    }

    /**
     * applies to jarray
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param index  ordinal position of the target value in the array
     * @param value  value in an array that has been accessed
     */
    default void get(Object target, String path, int index, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key    key used to identify a value in a dictionary
     * @param value  value in the dictionary that has been accessed
     */
    default void get(Object target, String path, String key, Object value) {
    }

    /**
     * applies to jarray - replace in an array using a predicate function
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value  the value in the array which has been updated
     */
    default void replace(Object target, String path, Object value) {
    }

    /**
     * applies to jarray
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param index  ordinal position of the target value in the array
     * @param value  the value in the array which has been updated
     */
    default void replace(Object target, String path, int index, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key    key used to identify a value in a dictionary
     * @param value  the value identified by the key 'key' in the dictionary which has been updated
     */
    default void replace(Object target, String path, String key, Object value) {
    }

    /**
     * applies to jarray - removal from an array using a predicate function
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value  a predicate function used to search for a target value for deletion in the array
     */
    default void delete(Object target, String path, Object value) {
    }

    /**
     * applies to jarray
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param index  ordinal position of the value in the array
     * @param value  value in the array that has been removed
     */
    default void delete(Object target, String path, int index, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents immediate parent of the JSON attribute whose data is being changed
     * @param path   represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key    key used to identify a value in a dictionary
     * @param value  value in the dictionary that has been removed
     */
    default void delete(Object target, String path, String key, Object value) {
    }

    /**
     * @param target unique identifier associated with the root resource undergoing modification
     * @param data   the stringified version of the change data payload
     */
    default void write(String target, String data) {
    }

    /**
     * @param target unique identifier associated with the root resource undergoing modification
     * @param event  name of event to be associated with the write operation
     * @param data   the stringified version of the change data payload
     */
    default void write(String target, String event, String data) {
    }
}
