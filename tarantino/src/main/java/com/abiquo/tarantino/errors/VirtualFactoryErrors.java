package com.abiquo.tarantino.errors;

public enum VirtualFactoryErrors
{

    /***
     * TODO
     */

   HYPERVISOR_CONNECTION("# Invalid hypervisor (ip, port or protocol) location.");
   
   
   
   /**
    * Internal error code
    */
   String code;

   /**
    * Description message
    */
   String message;

   public String getCode()
   {
       return String.valueOf(this.code);
   }

   public String getMessage()
   {
       return this.message;
   }

   VirtualFactoryErrors(String code, String message)
   {
       this.code = code;
       this.message = message;
   }

}
