CREATE TABLE IF NOT EXISTS public.employee (
                                 uuid uuid NOT NULL,
                                 birthday date NULL,
                                 date_created timestamptz(6) NOT NULL,
                                 email varchar(255) NULL,
                                 full_name varchar(255) NULL,
                                 hobbies varchar(255) NULL,
                                 last_updated timestamptz(6) NOT NULL,
                                 CONSTRAINT employee_pkey PRIMARY KEY (uuid),
                                 CONSTRAINT uk_fopic1oh5oln2khj8eat6ino0 UNIQUE (email)
);