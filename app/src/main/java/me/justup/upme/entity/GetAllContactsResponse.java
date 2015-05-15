package me.justup.upme.entity;

import java.util.ArrayList;
import java.util.List;


public class GetAllContactsResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public List<Referrals> referrals = new ArrayList<>();
        public List<Parents> parents = new ArrayList<>();

        public class Parents extends BaseHttpParams {
            public int id = 0;
            public int parent_id = 0;
            public String name = "";
            public String jabber_id = "";
            public int dateAdd = 0;
            public String login = "";
            public String phone = "";
            public String img = "";
            public double latitude = 0.0;
            public double longitude = 0.0;
            public int level = 0;
            public String in_system = "";
            public int total_sum = 0;
            private boolean status;

            public int getStatus() {
                if (status) return 1;
                else return 0;
            }

            @Override
            public String toString() {
                return "Parents{" +
                        "id=" + id +
                        ", parent_id=" + parent_id +
                        ", name=" + name +
                        ", jabber_id=" + jabber_id +
                        ", dateAdd=" + dateAdd +
                        ", login=" + login +
                        ", phone=" + phone +
                        ", img=" + img +
                        ", latitude=" + latitude +
                        ", longitude=" + longitude +
                        ", level=" + level +
                        ", in_system=" + in_system +
                        ", total_sum=" + total_sum +
                        ", status=" + status +
                        '}';
            }
        }

        public class Referrals extends Parents {
        }

        public List<Parents> getAllUsers() {
            if (referrals != null && referrals.isEmpty()) {
                return parents;
            }

            if (parents.addAll(referrals)) {
                return parents;
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "referrals=" + referrals +
                    ", parents=" + parents +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetAllContactsResponse{" +
                "result=" + result +
                '}';
    }

}
