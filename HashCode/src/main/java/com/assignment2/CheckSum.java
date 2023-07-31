package com.assignment2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by arulsmv on 26/7/23.
 */
public class CheckSum {
    // Using the DigestUtils ha256. This should be good sha for simple class with few fields.
    // the probaility of two different objects (strings) having same sha256 is one in billion.
    // Different sha algorithms or with a key DES, AES can be used.
    // It expects all the fiels have the method getFiledName method and the return type is convered to string.
    public String computechecksum(String str) {
        return DigestUtils.sha256Hex(str);
    }

    private String getStringforField(Field f, Person p) throws Exception {
        try {
            if ( f.getType().getCanonicalName().equals("com.assignment2.CheckSum") ) {
                return "";
            }
            Method method = p.getClass().getMethod("get" + StringUtils.capitalize(f.getName()));
            String s;
            Object val = method.invoke(p);
            return val.toString() + "@" + f.getName();
        } catch (NoSuchMethodException e) {
            throw new Exception("Error in hash computation", e );
        } catch (InvocationTargetException e) {
            throw new Exception("Error in hash computation", e );
        } catch (IllegalAccessException e) {
            throw new Exception("Error in hash computation", e );
        }
    }

    public String md5(Person p) throws Exception {
        String str = p.getClass().getCanonicalName();
        // I'm not happy with this way of getting the fields.
        // Not sure why c.getFields is returning empty array. my earlier understanding is it will return all the
        // fileds declared for the class including base/super class.
        // For this assignment there are only one base/super class and one derived class;
        // otherwise to concatinate the fieds needed to recursively traverse;
        // Given a class getDeclaredFields will return the fields in the same order for evey time it is invoked.
        // there will be no randomness. if there are any, explicit calling of the methods is required to compute the hash
        if ( str.equals("com.assignment2.Employee") ){
            for (Field f: p.getClass().getSuperclass().getDeclaredFields()) {
                str += ":" + getStringforField(f, p);
            }
        }
        for (Field f: p.getClass().getDeclaredFields()) {
            str += ":" + getStringforField(f, p);
        }
        return computechecksum(str);
    }
}
