package com.algorithm.customhash;

class CustomHashMap{
	static class HashMap {
        String key;
        int value;
        HashMap next;
    
        public HashMap(String key, int value, HashMap next){
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
	
    private HashMap[] hashTable;   //Array of HashMap.
    private int capacity;  //Initial capacity of HashMap
    
    
    
   public CustomHashMap(int size){
	   this.capacity = size;
      hashTable = new HashMap[capacity];
   }
   
   public void insert(String newKey, int data){
       if(newKey==null)
           return;   
      
       //calculate hash of key.
       int hash=hash(newKey);
       //create new hashMap.
       HashMap newHashMap = new HashMap(newKey, data, null);
      
       //if hashTable location does not contain any hashMap, store hashMap there.
        if(hashTable[hash] == null){
         hashTable[hash] = newHashMap;
        }else{
           HashMap previous = hashTable[hash];
           HashMap current = hashTable[hash].next;
           
           if(previous.key.equals(newKey))
        	   previous.value = previous.value+1;
           else{
        	   boolean flag = false;
        	   while(current!=null){
            	   if(current.key.equals(newKey)){
            		   current.value = current.value+1;
            		   flag = true;
            		   break;
            	   }
            	   else{
            		   current = current.next;
            		   previous = previous.next;
            	   }
               }
        	   if(!flag){
        		   previous.next = newHashMap;
        	   }
           }
        }
   }
          
   public int find(String key){
       int hash = hash(key);
       if(hashTable[hash] == null){
        return 0;
       }else{
        HashMap temp = hashTable[hash];
        while(temp!= null){
            if(temp.key.equals(key))
                return temp.value;
            temp = temp.next; 
        }         
        return -1;  
       }
   }

   
public boolean delete(String deleteKey){
       
       int hash=hash(deleteKey);
              
      if(hashTable[hash] == null){
            return false;
      }else{
        HashMap previous = null;
        HashMap current = hashTable[hash];
        
        while(current != null){ 
           if(current.key.equals(deleteKey)){               
               if(previous==null){ 
                     hashTable[hash]=hashTable[hash].next;
                     return true;
               }
               else{
                     previous.next=current.next;
                      return true;
               }
           }
           previous=current;
             current = current.next;
          }
        return false;
      }
    
    }

public void findAllKeys(){
    
    for(int i=0;i<capacity;i++){
        if(hashTable[i]!=null){
               HashMap hashMap=hashTable[i];
               while(hashMap!=null){
                     System.out.print("{"+hashMap.key+"="+hashMap.value+"}" +" ");
                     hashMap=hashMap.next;
               }
        }
    }             
 
 }

private int hash(String key){
    return Math.abs(key.hashCode()) % capacity;
}

}