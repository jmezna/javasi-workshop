var app = new Vue({
    el: '#app',
    data: {
        orderHealthy: false,
        infected: false,
        orders: [],
        customers: []
    },
    created () {
        this.fetchData();
        this.checkHealth();
    },
    methods: {
        fetchData () {
            fetch("//localhost:8080/v1/customers")
                .then(res => res.json())
                .then(data => this.customers = data);

            fetch("//localhost:8081/v1/orders")
                .then(res => res.json())
                .then(data => {

                    data.forEach(d => {

                        fetch("//localhost:8080/v1/customers/" + d.customerId)
                            .then((res) => res.json())
                            .then(c => {
                                d.customerName = c.firstName + " " + c.lastName;

                                this.orders = data.slice();

                                this.$nextTick();
                            })
                    });

                    this.orders = data;
                });
        },

        loadOrder() {
            fetch("//openshift.cafe-babe.org:31168/v1/load",
                {
                    method: 'post',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(45)
                });
        },

        infectOrder() {
            fetch("//openshift.cafe-babe.org:30812/v1/management/healthy",
                {
                    method: 'post',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(false)
                })
                .then((res) => {

                    if (res.ok) {
                        this.infected = true;
                        this.checkHealth();
                    }
                });
        },

        healOrder() {
            fetch("//openshift.cafe-babe.org:30812/v1/management/healthy",
                {
                    method: 'post',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(true)
                })
                .then((res) => {

                    if (res.ok) {
                        this.infected = false;
                        this.checkHealth();
                    }
                });
        },

        checkHealth() {
            fetch("//openshift.cafe-babe.org:30812/health").then((res) => {
                    console.log(res.ok);
                    this.orderHealthy = !!res.ok;
                }).catch(() => {
                    this.orderHealthy = false;
                });
        }
    }
});

