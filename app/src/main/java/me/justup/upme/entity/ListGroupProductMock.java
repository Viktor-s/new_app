package me.justup.upme.entity;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;

/**
 * Created by bogdan on 28.01.15.
 */
// TODO WTF?
public class ListGroupProductMock {

    private List<GroupProductEntity> listGroupProduct;

    private static volatile ListGroupProductMock instance;

    private ListGroupProductMock(Context context) {

        ProductEntityMock p11 = new ProductEntityMock(1, "ДЕНЬГИ В КОШЕЛЕК", "Сумма: до 3000 грн\n" +
                "Срок кредитования: до 30 дней\n" +
                "Условия: Без справки о доходах\n" +
                "Документы: Паспорт + ИНН");
        ProductEntityMock p12 = new ProductEntityMock(1, "ВРЕМЯ ДЕНЬГИ", "Сумма: до 100 000 грн\n" +
                "Срок кредитования: до 1 года\n" +
                "Условия: Подтвержденный доход\n" +
                "Документы: Паспорт + ИНН");
        ProductEntityMock p21 = new ProductEntityMock(1, "АВТОТРАНСПОРТ", "Документы: Паспорт + ИНН, Свидетелство\n" +
                "о регистрации автомобиля, генеральная\n" +
                "доверенность при отустсвии собственника.\n" +
                "Транспортное средство.");
        ProductEntityMock p22 = new ProductEntityMock(1, "НЕВДИЖИМОСТЬ", "Документы: Паспорт, ИНН, \n" +
                "правоустанавливающие документы, технический паспорт, форма №3.\n" +
                "Предмет залога");
        ProductEntityMock p31 = new ProductEntityMock(1, "ПЕНСИОННЫЙ ФОНД", "Накопления денежных средств для достойной жизни на пенсии");
        ProductEntityMock p32 = new ProductEntityMock(1, "НАСЛЕДНИК", "Целевое накопление денежных средств к какой либо дате в жизни ребенка");
        ProductEntityMock p41 = new ProductEntityMock(1, "ОСАГО", "Обязательное страхование гражданско- правовой ответственности владельцев транспортных средств(Автоцивилка) - это вид страхования, обеспечивающий возмещение вреда, причиненного жизни, здоровью или имуществу третьих лиц в результате дорожно- транспортного происшествия при эксплуатации транспортного средства на территории Украины.");
        ProductEntityMock p42 = new ProductEntityMock(1, "КАСКО", "Добровольное страхование транспортных средств от повреждения, уничтожения, потери в результате дорожно-транспортного происшествия, незаконного овладения, пожара, взрыва, стихийного бедствия, незаконных действия третьих лиц, внешнего воздействия посторонних предметов.");
        ProductEntityMock p51 = new ProductEntityMock(1, "БИЛЕТЫ", "Билеты");
        ProductEntityMock p61 = new ProductEntityMock(1, "MINI MBA LIGHT", "Срок обучения: 1 месяц / 110 часов\n" +
                "Уровень: Новичок;\n" +
                "Аудитория: Менеджеры низшего звена;\n" +
                "Программа: 1 учебный модуль + 1 Тест;");
        ProductEntityMock p62 = new ProductEntityMock(1, "MINI MBA DISTANCE", "Срок обучения: 3 месяца / 210 часов\n" +
                "Уровень: Специалист;\n" +
                "Аудитория: Менеджеры среднего звена;\n" +
                "Программа: 5 учебных модулей + 5 тестов");


        List<ProductEntityMock> listP1 =  new ArrayList<>();
        listP1.add(p11);
        listP1.add(p12);
        GroupProductEntity gp1 = new GroupProductEntity(1, 1, context.getResources().getString(R.string.product_title_credit_without_bail), context.getResources().getString(R.string.product_description_credit_without_bail), listP1);

        List<ProductEntityMock> listP2 =  new ArrayList<>();
        listP2.add(p21);
        listP2.add(p22);
        GroupProductEntity gp2 = new GroupProductEntity(2, 1, context.getResources().getString(R.string.product_title_credit_bail), context.getResources().getString(R.string.product_description_credit_bail), listP2);

        List<ProductEntityMock> listP3 =  new ArrayList<>();
        listP2.add(p31);
        listP2.add(p32);
        GroupProductEntity gp3 = new GroupProductEntity(3, 2, context.getResources().getString(R.string.product_title_life_insurance), context.getResources().getString(R.string.product_description_life_insurance), listP3);

        List<ProductEntityMock> listP4 =  new ArrayList<>();
        listP2.add(p41);
        listP2.add(p42);
        GroupProductEntity gp4 = new GroupProductEntity(4, 2, context.getResources().getString(R.string.product_title_credit_risk_insurance), context.getResources().getString(R.string.product_description_credit_risk_insurance), listP4);

        List<ProductEntityMock> listP5 =  new ArrayList<>();
        listP2.add(p51);
        GroupProductEntity gp5 = new GroupProductEntity(5, 3, context.getResources().getString(R.string.product_title_airline_tickets), context.getResources().getString(R.string.product_description_airline_tickets), listP5);

        List<ProductEntityMock> listP6 =  new ArrayList<>();
        listP2.add(p61);
        listP2.add(p62);
        GroupProductEntity gp6 = new GroupProductEntity(6, 4, context.getResources().getString(R.string.product_title_credit_education_online), context.getResources().getString(R.string.product_description_credit_education_online), listP6);

        listGroupProduct = new ArrayList<>();
        listGroupProduct.add(gp1);
        listGroupProduct.add(gp2);
        listGroupProduct.add(gp3);
        listGroupProduct.add(gp4);
        listGroupProduct.add(gp5);
        listGroupProduct.add(gp6);


    }

    public static ListGroupProductMock getInstance(Context context) {
        if (instance == null)
            synchronized (ListGroupProductMock.class) {
                if (instance == null)
                    instance = new ListGroupProductMock(context);
            }
        return instance;
    }


    public List<GroupProductEntity> getListGroupProduct() {
        return listGroupProduct;
    }

    public List<GroupProductEntity> getGroupProductByIdCategory(int idCategory) {
        List<GroupProductEntity> resultList = new ArrayList<>();
        for (GroupProductEntity groupProductEntity : listGroupProduct)
            if (groupProductEntity.getParentId() == idCategory)
                resultList.add(groupProductEntity);
        return resultList;
    }

    public GroupProductEntity getGroupProductById(int idGroupProduct) {
        for (GroupProductEntity groupProductEntity : listGroupProduct)
            if (groupProductEntity.getId() == idGroupProduct)
                return groupProductEntity;
        return null;
    }
}
