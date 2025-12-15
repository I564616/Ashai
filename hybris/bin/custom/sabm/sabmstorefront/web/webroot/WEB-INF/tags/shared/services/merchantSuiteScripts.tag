<%@ tag language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">

(function(e) {
    e.Version = "3";
    e.BaseUrl = "";
    e.SetupPayment = function(a) {
        var b = new d(!0, "Card Number"),
            g = new d(!0, "Expiry Date"),
            f = new d(!0, "CVN"),
            m = new d(!0, "Cardholder Name"),
            S = new d(!0, "Process Payment"),
            r = {
                AppendToElementId: null,
                AuthKey: null,
                PaymentReason: new d(!1, "Biller Code"),
                Reference1: new d(!1, "CRN1"),
                Reference2: new d(!1, "CRN2"),
                Reference3: new d(!1, "CRN3"),
                InternalNote: new d(!1, "Merchant Reference"),
                EmailAddress: new d(!1, "Email"),
                Currency: new d(!1,
                    "Currency"),
                Amount: new d(!1, "Amount"),
                StoreCard: new d(!1, "Store Card", !1),
                DefaultErrorUrl: null,
                Type: c.paymentCCOptionValue,
                TruncatedCardNumberValue: null,
                ExpiryDateValue: null
            };
        r = y(!0, r, a);
        var w = 0 > e.BaseUrl.search(/\/$/) ? e.BaseUrl += "/" : e.BaseUrl;
        a = M(r.AppendToElementId);
        var k = document.createElement("div"),
            l = document.createElement("div"),
            q = document.createElement("div"),
            t = [],
            p = new d(!0, "", r.AuthKey),
            z = new d(!0, "", r.DefaultErrorUrl),
            x = new d(!0, "", r.Type);
        N(w + "MerchantSuite/api.css?v=" + e.Version);
        k.id = n(c.container);
        l.id = n(c.form);
        q.id = n(c.validation);
        q.style.display = "none";
        t.push(new h("hidden", c.authKey, p));
        t.push(new h("hidden", c.defaultErrorUrl, z));
        t.push(new h("hidden", c.paymentProcessingType, x));
        t.push(new h("text", c.billerCode, r.PaymentReason, 50));
        t.push(new h("text", c.crn1, r.Reference1, 50));
        t.push(new h("text", c.crn2, r.Reference2, 50));
        t.push(new h("text", c.crn3, r.Reference3, 50));
        t.push(new h("text", c.merchantReference, r.InternalNote, 50));
        t.push(new h("text", c.email,
            r.EmailAddress, 250));
        t.push(new h("text", c.currency, r.Currency));
        t.push(new h("text", c.amount, r.Amount, 20));
        r.Type == c.paymentCCOptionValue ? t.push(new h("text", c.cardNumber, b, 19)) : r.Type == c.paymentTOKENOptionValue && (0 == e.IsNullOrEmptyString(r.TruncatedCardNumberValue) && (b = new d(!0, "Card Number", r.TruncatedCardNumberValue), t.push(new h("static-text", c.truncatedCardNumber, b))), 0 == e.IsNullOrEmptyString(r.ExpiryDateValue) && (g.Value = r.ExpiryDateValue));
        t.push(new h("expiry", c.expiryDate, g, 7));
        t.push(new h("cvc",
            c.cvc, f, 4));
        t.push(new h("text", c.cardHolderName, m, 50));
        t.push(new h("checkbox", c.storeCard, r.StoreCard));
        t.push(new h("button", c.submitButton, S));
        E(l, t);
        k.appendChild(q);
        k.appendChild(l);
        a.appendChild(k);
        document.getElementById(n(c.submitButton)).onclick = U
    };
    e.ProcessPayment = function(a) {
        var b = {
            AuthKey: null,
            Amount: null,
            AmountOriginal: null,
            AmountSurcharge: null,
            PaymentReason: null,
            Reference1: null,
            Reference2: null,
            Reference3: null,
            InternalNote: null,
            EmailAddress: null,
            Currency: null,
            CardHolderName: null,
            CardNumber: null,
            ExpiryMonth: null,
            ExpiryYear: null,
            CVN: null,
            StoreCard: !1,
            CallbackFunction: null,
            Type: c.paymentCCOptionValue
        };
        a = b = y(b, a);
        var g = [];
        "function" !== typeof a.CallbackFunction && g.push(new p("CallbackFunction is required", "CallbackFunction"));
        e.IsNullOrEmptyString(a.AuthKey) && g.push(new p("AuthKey is required", "AuthKey"));
        a.Type != c.paymentTOKENOptionValue && 0 == e.ValidateCardNumber(a.CardNumber) && g.push(new p("Invalid card number", "CardNumber"));
        0 == e.ValidateCVC(a.CVN) &&
            g.push(new p("Invalid CVN", "CVN"));
        0 == e.ValidateExpiry(a.ExpiryMonth, a.ExpiryYear) && g.push(new p("Invalid expiry date", "ExpiryYear"));
        0 < g.length ? A(b, g) : (a = B("txns/withauthkey", b.AuthKey), C({
            url: a,
            data: {
                TxnReq: {
                    Amount: b.Amount,
                    AmountOriginal: b.AmountOriginal,
                    AmountSurcharge: b.AmountSurcharge,
                    PaymentReason: b.PaymentReason,
                    Reference1: b.Reference1,
                    Reference2: b.Reference2,
                    Reference3: b.Reference3,
                    Currency: b.Currency,
                    InternalNote: b.InternalNote,
                    EmailAddress: b.EmailAddress,
                    StoreCard: b.StoreCard,
                    CardDetails: {
                        CardHolderName: b.CardHolderName,
                        CardNumber: b.CardNumber,
                        CVN: b.CVN,
                        ExpiryDateMonth: b.ExpiryMonth,
                        ExpiryDateYear: b.ExpiryYear
                    }
                }
            },
            callbackFunction: b.CallbackFunction
        }))
    };
    e.SetupAddToken = function(a) {
        F(a, "Add Token");
        document.getElementById(n(c.submitButton)).onclick = V
    };
    e.ProcessAddToken = function(a) {
        var b = {
            AuthKey: null,
            Reference1: null,
            Reference2: null,
            Reference3: null,
            EmailAddress: null,
            Type: c.tokenCCOptionValue,
            CardHolderName: null,
            CardNumber: null,
            ExpiryMonth: null,
            ExpiryYear: null,
            BSBNumber: null,
            BankAccountNumber: null,
            BankAccountName: null,
            AcceptBankAccountTerms: !1,
            CallbackFunction: null
        };
        a = b = y(b, a);
        var g = [];
        "function" !== typeof a.CallbackFunction && g.push(new p("CallbackFunction is required", "CallbackFunction"));
        e.IsNullOrEmptyString(a.AuthKey) && g.push(new p("AuthKey is required", "AuthKey"));
        a.Type != c.tokenCCOptionValue && a.Type != c.tokenBAOptionValue && g.push(new p("Invalid token type. Valid values are either CREDITCARD or BANKACCOUNT",
            "Type"));
        a.Type == c.tokenCCOptionValue ? (0 == e.ValidateCardNumber(a.CardNumber) && g.push(new p("Invalid card number", "CardNumber")), 0 == e.ValidateExpiry(a.ExpiryMonth, a.ExpiryYear) && g.push(new p("Invalid expiry date", "ExpiryYear"))) : a.Type == c.tokenBAOptionValue && (0 == e.ValidateBSB(a.BSBNumber) && g.push(new p("Invalid bsb number", "BsbNumber")), 0 == e.ValidateAccountNumber(a.BankAccountNumber) && g.push(new p("Invalid account number", "BankAccountNumber")), e.IsNullOrEmptyString(a.BankAccountName) && g.push(new p("Account name is required",
            "BankAccountName")), 0 == a.AcceptBankAccountTerms && g.push(new p("You must accept terms for adding bank account token", "AcceptBankAccountTerms")));
        0 < g.length ? A(b, g) : (a = B("tokens/withauthkey", b.AuthKey), g = G(b), C({
            url: a,
            data: g,
            callbackFunction: b.CallbackFunction
        }))
    };
    e.SetupUpdateToken = function(a) {
        F(a, "Update Token");
        document.getElementById(n(c.submitButton)).onclick = W
    };
    e.ProcessUpdateToken = function(a) {
        var b = {
            AuthKey: null,
            Reference1: null,
            Reference2: null,
            Reference3: null,
            EmailAddress: null,
            Type: c.tokenCCOptionValue,
            CardHolderName: null,
            CardNumber: null,
            ExpiryMonth: null,
            ExpiryYear: null,
            BSBNumber: null,
            BankAccountNumber: null,
            BankAccountName: null,
            AcceptBankAccountTerms: !1,
            CallbackFunction: null
        };
        a = b = y(b, a);
        var g = [];
        "function" !== typeof a.CallbackFunction && g.push(new p("CallbackFunction is required", "CallbackFunction"));
        e.IsNullOrEmptyString(a.AuthKey) && g.push(new p("AuthKey is required", "AuthKey"));
        a.Type != c.tokenCCOptionValue && a.Type != c.tokenBAOptionValue && g.push(new p("Invalid token type. Valid values are either CREDITCARD or BANKACCOUNT",
            "Type"));
        if (a.Type == c.tokenCCOptionValue) {
            if (0 == e.IsNullOrEmptyString(a.CardNumber) || 0 == e.IsNullOrEmptyString(a.ExpiryMonth + a.ExpiryYear)) 0 == e.ValidateCardNumber(a.CardNumber) && g.push(new p("Invalid card number", "CardNumber")), 0 == e.ValidateExpiry(a.ExpiryMonth, a.ExpiryYear) && g.push(new p("Invalid expiry date", "ExpiryYear"))
        } else a.Type != c.tokenBAOptionValue || 0 != e.IsNullOrEmptyString(a.BSBNumber) && 0 != e.IsNullOrEmptyString(a.BankAccountNumber) && 0 != e.IsNullOrEmptyString(a.BankAccountName) || (0 == e.ValidateBSB(a.BSBNumber) &&
            g.push(new p("Invalid bsb number", "BsbNumber")), 0 == e.ValidateAccountNumber(a.BankAccountNumber) && g.push(new p("Invalid account number", "BankAccountNumber")), e.IsNullOrEmptyString(a.BankAccountName) && g.push(new p("Account name is required", "BankAccountName")), 0 == a.AcceptBankAccountTerms && g.push(new p("You must accept terms for adding bank account token", "AcceptBankAccountTerms")));
        0 < g.length ? A(b, g) : (a = B("tokens/withauthkey/update", b.AuthKey), g = G(b), C({
            url: a,
            data: g,
            callbackFunction: b.CallbackFunction
        }))
    };
    e.ValidateCardNumber = function(a) {
        a = (a + "").replace(/\s+|-/g, "");
        var b;
        if (b = 10 <= a.length && 16 >= a.length)
            if (/[^0-9-\s]+/.test(a)) b = !1;
            else {
                b = 0;
                var c = !1;
                a = a.replace(/\D/g, "");
                for (var f = a.length - 1; 0 <= f; f--) {
                    var m = a.charAt(f);
                    m = parseInt(m, 10);
                    c && 9 < (m *= 2) && (m -= 9);
                    b += m;
                    c = !c
                }
                b = 0 == b % 10
            } return b
    };
    e.ValidateCVC = function(a) {
        a = x(a);
        return /^\d+$/.test(a) && 3 <= a.length && 4 >= a.length
    };
    e.ValidateExpiry = function(a, b) {
        a = x(a);
        b = x(b);
        if (0 == /^\d+$/.test(a) || 0 == /^\d+$/.test(b)) return !1;
        if ("99" === a && 2 === b.length) return !0;
        if (0 ==
            12 >= parseInt(a, 10)) return !1;
        2 == b.length && (b = "20" + b);
        b = new Date(b, a);
        a = new Date;
        b.setMonth(b.getMonth() - 1);
        b.setMonth(b.getMonth() + 1, 1);
        return b > a
    };
    e.ValidateBSB = function(a) {
        a = x(a);
        return /^\d+$/.test(a) && 6 == a.length
    };
    e.ValidateAccountNumber = function(a) {
        a = x(a);
        return /^\d+$/.test(a) && 3 <= a.length && 9 >= a.length
    };
    e.IsNullOrEmptyString = function(a) {
        return void 0 === a || null == a ? !0 : "string" === typeof a && 0 < a.length ? !1 : !0
    };
    var M = function(a) {
            a = void 0 === a ? null : a;
            return null == a || null == document.getElementById(a) ? document.getElementsByTagName("body")[0] :
                document.getElementById(a)
        },
        E = function(a, b) {
            var g = b.length,
                f = document.createElement("div"),
                m = null,
                e = null,
                k = "";
            f.className = n(c.row);
            for (var w = 0; w < g; w++) {
                var d = b[w].type,
                    h = "expiry" == d || "cvc" == d ? f : document.createElement("div"),
                    q = document.createElement("div"),
                    l = X(b[w]),
                    p = n(c.columnSm) + "-12";
                "radio" == d && (null == e || null == m ? (e = q, m = h, k = b[w].id) : k == b[w].id ? (q = e, h = m) : (e = q, m = h, k = b[w].id));
                "button" == d ? p += " " + n(c.columnMd) + "-4" : "expiry" == d || "cvc" == d || "bsb" == d ? p = n(c.columnSm) + "-6" : "static-text" == d && (p = "api-horrizontal");
                null != l && ("hidden" == d ? a.appendChild(l) : null != h && null != q && (h.className = n(c.row), q.className = p, q.appendChild(l), h.appendChild(q), a.appendChild(h)))
            }
        },
        X = function(a) {
            if (a.displayObject.Visible) {
                var b = document.createElement("div"),
                    g = document.createElement("label"),
                    f = document.createElement("input"),
                    m = Y(a.type),
                    k = n(a.id),
                    d = a.displayObject.LabelName,
                    h = a.displayObject.Value,
                    l = a.displayObject.ReadOnly;
                b.className = n(c.node);
                "static-text" != m && f.setAttribute("type", m);
                if ("text" == m) {
                    var p = parseInt(a.maxlength,
                        10);
                    g.setAttribute("for", k);
                    g.innerHTML = d;
                    b.appendChild(g);
                    f.setAttribute("autocomplete", "off");
                    f.setAttribute("value", h);
                    (l = e.IsNullOrEmptyString(h) ? !1 : l) && f.setAttribute("readonly", "readonly");
                    "expiry" == a.type && (f.setAttribute("placeholder", "MM / YY"), f.onkeyup = Z);
                    0 < p && f.setAttribute("maxlength", p)
                } else if ("hidden" == m) f.setAttribute("value", h);
                else if ("button" == m) f.setAttribute("value", d);
                else if ("checkbox" == m || "radio" == m) "radio" == m && f.setAttribute("value", a.radioValue), 1 == h && f.setAttribute("checked",
                    "checked"), l && f.setAttribute("disabled", "disabled"), g.appendChild(f), h = document.createElement("span"), h.innerHTML = d, g.appendChild(h);
                else if ("static-text" == m) return g.className = n(c.columnMd) + "-4 " + n(c.columnSm) + "-6 horrizontal-label", g.innerHTML = d, b.appendChild(g), g = document.createElement("div"), f = document.createElement("p"), g.className = n(c.columnMd) + "-4 " + n(c.columnSm) + "-6", f.className = n(m), f.innerHTML = h, g.appendChild(f), b.appendChild(g), b;
                "radio" == m ? f.name = k : f.id = k;
                if ("hidden" == m) return f;
                "checkbox" ==
                m || "radio" == m ? (b.className += " " + n(m), b.appendChild(g)) : (f.className = n(m), b.appendChild(f));
                return b
            }
            return null
        },
        Y = function(a) {
            switch (a) {
                case "hidden":
                    return "hidden";
                case "checkbox":
                    return "checkbox";
                case "button":
                    return "button";
                case "radio":
                    return "radio";
                case "static-text":
                    return "static-text";
                default:
                    return "text"
            }
        },
        k = function(a) {
            a = document.getElementById(n(a));
            return null == a ? {} : "checkbox" == a.type ? a.checked : a.value
        },
        H = function(a) {
            a = document.getElementsByName(a);
            for (var b = "", c = a.length, f = 0; f < c; f++)
                if (a[f].checked) {
                    b =
                        a[f].value;
                    break
                } return b
        },
        I = function(a) {
            for (var b = document.getElementById(n(a)).getElementsByTagName("input"), c = 0; c < b.length; c++) a = b[c], a.className = a.className.replace(new RegExp("\\b" + n("text-error") + "\\b"), "")
        },
        O = function(a) {
            for (a = document.getElementById(n(a)); a.firstChild;) a.removeChild(a.firstChild)
        },
        N = function(a) {
            var b = document.createElement("link"),
                c = document.getElementsByTagName("head")[0];
            b.setAttribute("type", "text/css");
            b.setAttribute("rel", "stylesheet");
            b.setAttribute("href", a);
            c.appendChild(b)
        },
        Z = function(a) {
            var b = this.value;
            var c = new RegExp(/^\d{1,2}\s?\/\s?\d{2}$/);
            8 !== a.keyCode && 46 !== a.keyCode && 37 !== a.keyCode && 39 !== a.keyCode && (b = b.replace(/[^0-9\/\s]/g, ""), 0 == c.test(b) && 2 == b.length && (a = b.substring(0, 1), c = b.substring(1, 2), a = "/" == c || /\s/.test(c) ? "0" + a : b, b = a + " / "), this.value = b)
        },
        A = function(a, b) {
            b = {
                AjaxResponseType: 1,
                ApiResponseCode: null,
                Errors: b,
                RedirectionUrl: null,
                ResultKey: null
            };
            "function" === typeof a.CallbackFunction && a.CallbackFunction(b)
        },
        B = function(a, b) {
            return (0 > e.BaseUrl.search(/\/$/) ?
                e.BaseUrl += "/" : e.BaseUrl) + "v" + e.Version + "/" + a + "/" + b
        },
        F = function(a, b) {
            var g = new d(!0, "Card Number"),
                f = new d(!0, "Expiry Date"),
                m = new d(!0, "Cardholder Name"),
                k = new d(!0, "BSB"),
                l = new d(!0, "Account Number"),
                p = new d(!0, "Account Name"),
                T = new d(!0, "Credit Card", !0),
                x = new d(!0, "Bank Account", !1);
            b = new d(!0, b);
            var q = {
                AppendToElementId: null,
                AuthKey: null,
                Reference1: new d(!1, "CRN1"),
                Reference2: new d(!1, "CRN2"),
                Reference3: new d(!1, "CRN3"),
                EmailAddress: new d(!1, "Email"),
                Type: c.tokenCCOptionValue,
                AcceptBankAccountTerms: !1,
                DefaultErrorUrl: null
            };
            q = y(!0, q, a);
            a = 0 > e.BaseUrl.search(/\/$/) ? e.BaseUrl += "/" : e.BaseUrl;
            var t = M(q.AppendToElementId),
                v = document.createElement("div"),
                z = document.createElement("div"),
                A = document.createElement("div"),
                B = document.createElement("div"),
                J = document.createElement("div"),
                C = document.createElement("div"),
                u = [],
                K = [],
                L = [],
                D = [],
                F = new d(!0, "", q.AuthKey),
                G = new d(!0, "", q.DefaultErrorUrl),
                H = new d(!0, "", q.Type),
                I = new d(!0, "", q.AcceptBankAccountTerms);
            N(a + "MerchantSuite/api.css?v=" + e.Version);
            v.id =
                n(c.container);
            z.id = n(c.form);
            A.id = n(c.validation);
            A.style.display = "none";
            u.push(new h("hidden", c.authKey, F));
            u.push(new h("hidden", c.defaultErrorUrl, G));
            u.push(new h("hidden", c.tokenRecordType, H));
            u.push(new h("hidden", c.acceptBankTerms, I));
            u.push(new h("text", c.crn1, q.Reference1, 50));
            u.push(new h("text", c.crn2, q.Reference2, 50));
            u.push(new h("text", c.crn3, q.Reference3, 50));
            u.push(new h("text", c.email, q.EmailAddress, 250));
            q.Type == c.tokenOPTIONOptionValue && (u.push(new h("radio", c.tokenRecordType, T,
                null, c.tokenCCOptionValue)), u.push(new h("radio", c.tokenRecordType, x, null, c.tokenBAOptionValue)));
            E(z, u);
            if (q.Type == c.tokenOPTIONOptionValue || q.Type == c.tokenCCOptionValue) B.id = n(c.creditCardSection), K.push(new h("text", c.cardNumber, g, 19)), K.push(new h("expiry", c.expiryDate, f, 7)), K.push(new h("text", c.cardHolderName, m, 50)), E(B, K), z.appendChild(B);
            if (q.Type == c.tokenOPTIONOptionValue || q.Type == c.tokenBAOptionValue) J.id = n(c.bankAccountSection), q.Type == c.tokenOPTIONOptionValue && (J.style.display = "none"),
                L.push(new h("bsb", c.bsb, k, 6)), L.push(new h("text", c.bankAccount, l, 9)), L.push(new h("text", c.bankName, p, 32)), E(J, L), z.appendChild(J);
            D.push(new h("button", c.submitButton, b));
            E(C, D);
            z.appendChild(C);
            v.appendChild(A);
            v.appendChild(z);
            t.appendChild(v);
            if (q.Type == c.tokenOPTIONOptionValue)
                for (g = document.getElementsByName(n(c.tokenRecordType)), f = g.length, m = 0; m < f; m++) g[m].onclick = aa
        },
        aa = function() {
            var a = n(c.tokenRecordType),
                b = n(c.creditCardSection),
                g = n(c.bankAccountSection);
            a = H(a);
            b = document.getElementById(b);
            g = document.getElementById(g);
            a == c.tokenBAOptionValue ? (b.style.display = "none", g.style.display = "block") : (b.style.display = "block", g.style.display = "none")
        },
        U = function() {
            var a = document.getElementById(n(c.validation));
            this.setAttribute("value", "Processing...");
            this.setAttribute("disabled", "disabled");
            O(c.validation);
            a.style.display = "none";
            I(c.form);
            a = P(k(c.expiryDate));
            var b = k(c.storeCard);
            b = "boolean" === typeof b ? b : !1;
            e.ProcessPayment({
                AuthKey: k(c.authKey),
                Amount: k(c.amount),
                PaymentReason: k(c.billerCode),
                Reference1: k(c.crn1),
                Reference2: k(c.crn2),
                Reference3: k(c.crn3),
                InternalNote: k(c.merchantReference),
                EmailAddress: k(c.email),
                Currency: k(c.currency),
                CardHolderName: k(c.cardHolderName),
                CardNumber: k(c.cardNumber),
                ExpiryMonth: a.month,
                ExpiryYear: a.year,
                CVN: k(c.cvc),
                StoreCard: b,
                CallbackFunction: ba,
                Type: k(c.paymentProcessingType)
            })
        },
        ba = function(a) {
            D(a || {}, "Process Payment")
        },
        Q = function() {
            var a = document.getElementById(n(c.validation)),
                b = k(c.tokenRecordType),
                g = k(c.acceptBankTerms),
                f = {
                    recordType: b
                };
            O(c.validation);
            a.style.display = "none";
            I(c.form);
            f.expiry = P(k(c.expiryDate));
            f.acceptTerms = "true" == g ? !0 : !1;
            b == c.tokenOPTIONOptionValue && (a = n(c.tokenRecordType), f.recordType = H(a));
            return f
        },
        V = function() {
            var a = Q();
            this.setAttribute("value", "Processing...");
            this.setAttribute("disabled", "disabled");
            e.ProcessAddToken({
                AuthKey: k(c.authKey),
                Reference1: k(c.crn1),
                Reference2: k(c.crn2),
                Reference3: k(c.crn3),
                EmailAddress: k(c.email),
                Type: a.recordType,
                CardHolderName: k(c.cardHolderName),
                CardNumber: k(c.cardNumber),
                ExpiryMonth: a.expiry.month,
                ExpiryYear: a.expiry.year,
                BSBNumber: k(c.bsb),
                BankAccountNumber: k(c.bankAccount),
                BankAccountName: k(c.bankName),
                AcceptBankAccountTerms: a.acceptTerms,
                CallbackFunction: ca
            })
        },
        W = function() {
            var a = Q();
            this.setAttribute("value", "Processing...");
            this.setAttribute("disabled", "disabled");
            e.ProcessUpdateToken({
                AuthKey: k(c.authKey),
                Reference1: k(c.crn1),
                Reference2: k(c.crn2),
                Reference3: k(c.crn3),
                EmailAddress: k(c.email),
                Type: a.recordType,
                CardHolderName: k(c.cardHolderName),
                CardNumber: k(c.cardNumber),
                ExpiryMonth: a.expiry.month,
                ExpiryYear: a.expiry.year,
                BSBNumber: k(c.bsb),
                BankAccountNumber: k(c.bankAccount),
                BankAccountName: k(c.bankName),
                AcceptBankAccountTerms: a.acceptTerms,
                CallbackFunction: da
            })
        },
        ca = function(a) {
            D(a || {}, "Add Token")
        },
        da = function(a) {
            D(a || {}, "Update Token")
        },
        v = function(a) {
            var b = "",
                c = "",
                f = 999,
                m = [],
                e = {
                    responseType: 1,
                    responseObj: null,
                    callbackFunction: null
                };
            e = y(e, a);
            1 == e.responseType ? m.push(new p("Error submitting the request", "")) : 2 == e.responseType ? m.push(new p("Your request has timed out",
                "")) : (f = e.responseObj.ApiResponseCode, c = e.responseObj.RedirectionUrl, b = e.responseObj.ResultKey, 0 < f && m.push(new p(e.responseObj.ResponseText, ea(f))));
            a = {
                AjaxResponseType: e.responseType,
                ApiResponseCode: f,
                Errors: m,
                RedirectionUrl: c,
                ResultKey: b
            };
            "function" === typeof e.callbackFunction && e.callbackFunction(a)
        },
        ea = function(a) {
            switch (a) {
                case 105:
                    return "PaymentReason";
                case 106:
                    return "Reference1";
                case 107:
                    return "Reference2";
                case 108:
                    return "Reference3";
                case 109:
                    return "Currency";
                case 110:
                    return "Amount";
                case 111:
                    return "InternalNote";
                case 112:
                    return "CardNumber";
                case 113:
                    return "CardHolderName";
                case 114:
                    return "ExpiryYear";
                case 115:
                    return "CVN";
                case 124:
                    return "BankAccountNumber";
                case 125:
                    return "BsbNumber";
                case 126:
                    return "BankAccountName";
                case 127:
                    return "EmailAddress";
                case 130:
                    return "AmountOriginal";
                case 131:
                    return "AmountSurcharge";
                default:
                    return ""
            }
        },
        fa = function(a) {
            switch (a) {
                case "PaymentReason":
                    return c.billerCode;
                case "Reference1":
                    return c.crn1;
                case "Reference2":
                    return c.crn2;
                case "Reference3":
                    return c.crn3;
                case "Currency":
                    return c.currency;
                case "Amount":
                    return c.amount;
                case "InternalNote":
                    return c.merchantReference;
                case "CardNumber":
                    return c.cardNumber;
                case "CardHolderName":
                    return c.cardHolderName;
                case "ExpiryYear":
                    return c.expiryDate;
                case "CVN":
                    return c.cvc;
                case "BankAccountNumber":
                    return c.bankAccount;
                case "BsbNumber":
                    return c.bsb;
                case "BankAccountName":
                    return c.bankName;
                case "EmailAddress":
                    return c.email;
                default:
                    return ""
            }
        },
        G = function(a) {
            return "BANKACCOUNT" == a.Type ? {
                TokenReq: {
                    Reference1: a.Reference1,
                    Reference2: a.Reference2,
                    Reference3: a.Reference3,
                    EmailAddress: a.EmailAddress,
                    CardDetails: null,
                    AcceptBADirectDebitTC: a.AcceptBankAccountTerms,
                    BankAccountDetails: {
                        BSBNumber: a.BSBNumber,
                        AccountNumber: a.BankAccountNumber,
                        AccountName: a.BankAccountName
                    }
                }
            } : {
                TokenReq: {
                    Reference1: a.Reference1,
                    Reference2: a.Reference2,
                    Reference3: a.Reference3,
                    EmailAddress: a.EmailAddress,
                    CardDetails: {
                        CardHolderName: a.CardHolderName,
                        CardNumber: a.CardNumber,
                        ExpiryDateMonth: a.ExpiryMonth,
                        ExpiryDateYear: a.ExpiryYear
                    },
                    AcceptBADirectDebitTC: !1,
                    BankAccountDetails: null
                }
            }
        },
        D = function(a, b) {
            var g = document.getElementById(n(c.submitButton)),
                f = document.getElementById(n(c.validation)),
                m = k(c.defaultErrorUrl);
            g.setAttribute("value", b);
            g.removeAttribute("disabled");
            0 == a.AjaxResponseType ? 0 == a.ApiResponseCode ? window.location.href = a.RedirectionUrl : 100 <= a.ApiResponseCode && 200 > a.ApiResponseCode ? (a = R(a.Errors, !0), f.appendChild(a), f.style.display = "block") : 0 == e.IsNullOrEmptyString(a.RedirectionUrl) ?
                window.location.href = a.RedirectionUrl : window.location.href = m : (a = R(a.Errors, !0), f.appendChild(a), f.style.display = "block")
        },
        ha = function(a) {
            var b = "";
            null != a.TxnReq ? (b += l("CardNumber", a.TxnReq.CardDetails.CardNumber), b += l("&CVN", a.TxnReq.CardDetails.CVN), b += l("&ExpiryDateMonth", a.TxnReq.CardDetails.ExpiryDateMonth), b += l("&ExpiryDateYear", a.TxnReq.CardDetails.ExpiryDateYear),
                b += l("&CardHolderName", a.TxnReq.CardDetails.CardHolderName), b += l("&Amount", a.TxnReq.Amount), b += l("&AmountOriginal", a.TxnReq.AmountOriginal), b += l("&AmountSurcharge", a.TxnReq.AmountSurcharge), b += l("&PaymentReason", a.TxnReq.PaymentReason), b += l("&Reference1", a.TxnReq.Reference1), b += l("&Reference2", a.TxnReq.Reference2), b += l("&Reference3",
                    a.TxnReq.Reference3), b += l("&Currency", a.TxnReq.Currency), b += l("&InternalNote", a.TxnReq.InternalNote), b += l("&EmailAddress", a.TxnReq.EmailAddress), b += l("&StoreCard", a.TxnReq.StoreCard)) : null != a.TokenReq && (null != a.TokenReq.CardDetails ? (b += l("CardNumber", a.TokenReq.CardDetails.CardNumber), b += l("&ExpiryDateMonth",
                a.TokenReq.CardDetails.ExpiryDateMonth), b += l("&ExpiryDateYear", a.TokenReq.CardDetails.ExpiryDateYear), b += l("&CardHolderName", a.TokenReq.CardDetails.CardHolderName)) : null != a.TokenReq.BankAccountDetails && (b += l("BSBNumber", a.TokenReq.BankAccountDetails.BSBNumber), b += l("&AccountNumber", a.TokenReq.BankAccountDetails.AccountNumber), b += l("&AccountName",
                a.TokenReq.BankAccountDetails.AccountName)), b += l("&Reference1", a.TokenReq.Reference1), b += l("&Reference2", a.TokenReq.Reference2), b += l("&Reference3", a.TokenReq.Reference3), b += l("&EmailAddress", a.TokenReq.EmailAddress), b += l("&AcceptBADirectDebitTC", a.TokenReq.AcceptBADirectDebitTC));
            return b
        },
        x = function(a) {
            return (a + "").replace(/^\s+|\s+$/g, "")
        },
        y = function() {
            var a, b, c, f = arguments[0] || {},
                e = 1,
                k = arguments.length,
                h = !1;
            "boolean" === typeof f && (h = f, f = arguments[1] || {}, e = 2);
            for ("object" !== typeof f && "function" !== typeof f && (f = {}); e < k; e++)
                if (null != (a = arguments[e]))
                    for (b in a) {
                        var d = f[b];
                        var l = a[b];
                        f !== l && (h && l && ("object" === typeof l || (c = "[object Array]" === Object.prototype.toString.call(l))) ? (c ? (c = !1, d = d && "[object Array]" === Object.prototype.toString.call(d) ? d : []) : d = d && "object" === typeof d ? d : {}, f[b] = y(h, d, l)) : void 0 !== l && (f[b] = l))
                    }
            return f
        },
        n = function(a) {
            return ia + "-" + a
        },
        P = function(a) {
            var b = "",
                c = "";
            4 == a.length ? (b = a.substring(0,
                2), c = a.substring(2, 4)) : 5 == a.length ? (b = a.substring(0, 2), c = a.substring(3, 5)) : 7 == a.length && (b = a.substring(0, 2), c = a.substring(5, 7));
            return {
                month: b,
                year: c
            }
        },
        R = function(a, b) {
            var g = a.length,
                f = document.createElement("ul");
            b = void 0 === b ? !1 : b;
            f.className = n(c.errorList);
            for (var m = 0; m < g; m++) {
                var d = document.createElement("li");
                d.innerHTML = a[m].Message;
                f.appendChild(d);
                1 == b && (d = fa(a[m].PropertyName), 0 == e.IsNullOrEmptyString(d) && (d = document.getElementById(n(d)), null != d && (d.className += " " + n("text-error"))))
            }
            return f
        },
        l = function(a, b) {
            return 0 == e.IsNullOrEmptyString(b) || "boolean" === typeof b ? a + "=" + b : ""
        },
        ja = function(a, b) {
            var c = new XMLHttpRequest;
            "withCredentials" in c ? c.open(a, b, !0) : "undefined" != typeof XDomainRequest ? (c = new XDomainRequest, c.open(a, b + ".nv")) : c = null;
            return c
        },
        C = function(a) {
            var b = null,
                c = {},
                f = 0 == "withCredentials" in new XMLHttpRequest && "undefined" != typeof XDomainRequest,
                d = {
                    url: null,
                    method: "POST",
                    contentType: "application/json",
                    data: null,
                    callbackFunction: null
                };
            d = y(d, a);
            var h = ja(d.method, d.url);
            null == h ? v({
                    callbackFunction: d.callbackFunction
                }) :
                (h.timeout = 6E4, f ? (null != d.data && (b = ha(d.data)), h.onload = function() {
                        var a = 1;
                        if (0 == e.IsNullOrEmptyString(h.responseText)) {
                            a = 0;
                            for (var b = {}, f = h.responseText.split("&"), g = 0; g < f.length; g++) {
                                var k = f[g].split("="),
                                    m = k[0];
                                k = k[1] || "";
                                k = k.replace(/\+/g, "%20");
                                b[m] = decodeURIComponent(k)
                            }
                            c.ApiResponseCode = b.ResponseCode;
                            c.RedirectionUrl = b.RedirectionUrl;
                            c.ResultKey = b.ResultKey;
                            c.ResponseText = b.ResponseText
                        }
                        v({
                            responseType: a,
                            responseObj: c,
                            callbackFunction: d.callbackFunction
                        })
                    }, h.onprogress = function() {}) : (h.setRequestHeader("Content-type",
                        d.contentType), null != d.data && (b = JSON.stringify(d.data)), h.onload = function() {
                        var a = 1;
                        if (200 == h.status) {
                            a = 0;
                            var b = JSON.parse(h.response);
                            c.ApiResponseCode = b.APIResponse.ResponseCode;
                            c.RedirectionUrl = b.RedirectionUrl;
                            c.ResultKey = b.ResultKey;
                            c.ResponseText = b.APIResponse.ResponseText
                        }
                        v({
                            responseType: a,
                            responseObj: c,
                            callbackFunction: d.callbackFunction
                        })
                    }), h.onerror = function() {
                        v({
                            callbackFunction: d.callbackFunction
                        })
                    }, h.ontimeout = function() {
                        v({
                            responseType: 2,
                            callbackFunction: d.callbackFunction
                        })
                    },
                    h.send(b))
        },
        p = function(a, b) {
            this.Message = a;
            this.PropertyName = b
        },
        d = function(a, b, c, d) {
            this.Visible = void 0 === a ? !1 : a;
            this.LabelName = void 0 === b ? "" : b;
            this.Value = void 0 === c ? "" : c;
            this.ReadOnly = void 0 === d ? !1 : d
        },
        h = function(a, b, c, d, e) {
            this.type = a;
            this.id = b;
            this.displayObject = c;
            this.maxlength = void 0 === d ? 0 : d;
            this.radioValue = void 0 === e ? "" : e
        },
        ia = "api",
        c = {
            authKey: "authkey-copy",
            billerCode: "billercode",
            crn1: "crn1",
            crn2: "crn2",
            crn3: "crn3",
            merchantReference: "merchantreference",
            email: "emailaddress",
            currency: "currency",
            amount: "amount",
            cardNumber: "cardnumber",
            truncatedCardNumber: "truncatedcardnumber",
            expiryDate: "expirydate",
            cvc: "cvc",
            cardHolderName: "cardholdername",
            bsb: "bsbnumber",
            bankAccount: "bankaccountnumber",
            bankName: "bankaccountname",
            acceptBankTerms: "acceptbankterms",
            storeCard: "storecard",
            submitButton: "submitbutton",
            form: "form",
            validation: "validation",
            errorList: "error-list",
            node: "node",
            defaultErrorUrl: "defaulterrorurl",
            container: "container",
            row: "row",
            columnSm: "col-sm",
            columnMd: "col-md",
            tokenRecordType: "recordtype",
            paymentProcessingType: "paymentprocessingtype",
            creditCardSection: "creditcardsection",
            bankAccountSection: "bankaccountsection",
            tokenCCOptionValue: "CREDITCARD",
            tokenBAOptionValue: "BANKACCOUNT",
            tokenOPTIONOptionValue: "OPTION",
            paymentCCOptionValue: "CREDITCARD",
            paymentTOKENOptionValue: "TOKENPAYMENT"
        }
})(this.MerchantSuite = this.MerchantSuite || {});

</script>