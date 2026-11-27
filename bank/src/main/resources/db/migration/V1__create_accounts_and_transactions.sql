CREATE TABLE accounts (
                          id      VARCHAR(36)    PRIMARY KEY,
                          owner   VARCHAR(100)   NOT NULL,
                          balance NUMERIC(14, 2) NOT NULL DEFAULT 0
);

CREATE TABLE transactions (
                              id            BIGSERIAL      PRIMARY KEY,
                              account_id    VARCHAR(36)    NOT NULL REFERENCES accounts (id),
                              type          VARCHAR(20)    NOT NULL,
                              amount        NUMERIC(14, 2) NOT NULL,
                              balance_after NUMERIC(14, 2) NOT NULL,
                              created_at    TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_account_id ON transactions (account_id);