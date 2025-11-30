package org.openoa.base.constant.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum SupporttedDatabaseEnum implements AfEnumBase{
    CAN_NOT_DETERMAIN(-9999,"can not determain","can not determain",null),
    MYSQL(1,"mysql","MySQL",null),
    ORACLE(2,"oracle","Oracle" ,null),
    POSTGRESQL (3,"postgresql","PostgreSQL",null),
    SQLSERVER(4,"sqlserver","Microsoft SQL Server",null),
    OCEANBASE(5,"oracle","OceanBase",SQLSERVER),
    GAUSS(6,"postgresql","openGauss",POSTGRESQL),
    DM(7,"dm","dm",null),
    POLAR_DB(8,"polardb","polardb",null),
    KINGBASE(9,"kingbase","KingBase ES",null),
    GBASE(10,"gbase","gbase",null),
    MONGODB(11,"mongo","MongoDb",null),
    TiDb(12,"tidb","tidb server",null)
    ;
    private final Integer code;
    private final String databaseId;
    private final String desc;
    private final SupporttedDatabaseEnum share;
    ;


    SupporttedDatabaseEnum(Integer code,String databaseId, String databaseName,SupporttedDatabaseEnum share){

        this.code = code;
        this.databaseId = databaseId;
        this.desc = databaseName;
        this.share = share;
    }
    public static SupporttedDatabaseEnum getByDatabaseId(String databaseId){
        if(!StringUtils.hasText(databaseId)){
            return CAN_NOT_DETERMAIN;
        }
        for (SupporttedDatabaseEnum databaseEnum : SupporttedDatabaseEnum.values()) {
            if(databaseId.equals(databaseEnum.getDatabaseId())){
                return databaseEnum;
            }
        }
        return CAN_NOT_DETERMAIN;
    }
    public static SupporttedDatabaseEnum getByDatabaseName(String databaseName){
        if(!StringUtils.hasText(databaseName)){
            return CAN_NOT_DETERMAIN;
        }
        for (SupporttedDatabaseEnum databaseEnum : SupporttedDatabaseEnum.values()) {
            if(databaseName.equals(databaseEnum.getDesc())){
                if(databaseEnum.share!=null){
                    return databaseEnum.share;
                }
                return databaseEnum;
            }
        }
        return CAN_NOT_DETERMAIN;
    }
}
