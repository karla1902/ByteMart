"""empty message

Revision ID: 079a18225d06
Revises: 
Create Date: 2024-10-14 01:06:29.764927

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '079a18225d06'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('tarjeta',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('saldo', sa.Integer(), nullable=False),
    sa.Column('numero_tarjeta', sa.String(length=16), nullable=False),
    sa.Column('codigo_verificacion', sa.String(length=3), nullable=False),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('transacciones',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('tarjeta_id', sa.Integer(), nullable=False),
    sa.Column('monto', sa.Integer(), nullable=False),
    sa.Column('fecha', sa.DateTime(), nullable=True),
    sa.ForeignKeyConstraint(['tarjeta_id'], ['tarjeta.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('transacciones')
    op.drop_table('tarjeta')
    # ### end Alembic commands ###
