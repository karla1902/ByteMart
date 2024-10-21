"""empty message

Revision ID: 39baff45d279
Revises: 89605597ea08
Create Date: 2024-10-20 00:06:25.509117

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = '39baff45d279'
down_revision = '89605597ea08'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('tarjetas', schema=None) as batch_op:
        batch_op.add_column(sa.Column('mes_vencimiento', sa.Integer(), nullable=False))
        batch_op.add_column(sa.Column('anio_vencimiento', sa.Integer(), nullable=False))
        batch_op.drop_column('fecha_vencimiento')

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('tarjetas', schema=None) as batch_op:
        batch_op.add_column(sa.Column('fecha_vencimiento', mysql.VARCHAR(length=5), nullable=False))
        batch_op.drop_column('anio_vencimiento')
        batch_op.drop_column('mes_vencimiento')

    # ### end Alembic commands ###