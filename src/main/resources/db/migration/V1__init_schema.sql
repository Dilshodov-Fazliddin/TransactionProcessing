CREATE TYPE transaction_type AS ENUM (
    'P2P',
    'PAYMENT',
    'MERCHANT'
);

CREATE TYPE transaction_status AS ENUM (
    'CREATED',
    'SENDER_INFO_VALIDATED',
    'SENDER_INFO_VALIDATION_FAILED',
    'RECEIVER_INFO_VALIDATED',
    'RECEIVER_INFO_VALIDATION_FAILED',
    'AMOUNT_VALIDATED',
    'AMOUNT_VALIDATION_FAILED',
    'SENT_TO_CORE_LEDGER',
    'SUCCESS',
    'FAILED'
);

CREATE TYPE transaction_currency AS ENUM (
    'UZS',
    'RUB',
    'USD'
);

CREATE TABLE transactions (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    merchant_id bigint NOT NULL,
    terminal_id bigint NOT NULL,
    reference_id UUID NOT NULL UNIQUE,
    type transaction_type NOT NULL,
    status transaction_status NOT NULL DEFAULT 'CREATED',
    currency transaction_currency NOT NULL,
    amount bigint NOT NULL CHECK (amount > 0),
    fee bigint NOT NULL CHECK (fee >= 0),
    sender_account_id UUID,
    receiver_account_id UUID,
    sender_name varchar(255) NOT NULL,
    sender_token varchar(255) NOT NULL,
    receiver_name varchar(255) NOT NULL,
    receiver_token varchar(255) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);